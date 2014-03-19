import sbt._

object Build extends sbt.Build {
  lazy val testIncrementalCompile = TaskKey[Unit]("testIncrementalCompile")

  lazy val root = Project("sbt-test", file(".")).settings(
    testIncrementalCompile <<= (Keys.state, Keys.streams) map tic,
    Keys.incOptions ~= (_.copy(recompileAllFraction = 100).withNameHashing(false)),
    Keys.scalaVersion := "2.10.4-RC3",
    runFixed := {
     val _ = (Keys.run in Compile).toTask("").value
    }
  )
  lazy val runFixed = taskKey[Unit]("A task that hard codes the values to `run`")

  def tic(state: State, streams: Keys.TaskStreams): Unit = {
    val extracted = Project.extract(state)
    def compile = extracted.runTask(Keys.compile in Compile, state)
    def run = extracted.runTask(runFixed, state)
    val (_, sources) = extracted.runTask(Keys.sources in Compile, state)
    val mods = for (source <- sources) yield {
      val modFile = file(source.absolutePath + ".mod")
      if (modFile.exists()) {
        val _source = source
        Some(new {
          val source = _source
          val newSource = IO.read(modFile)
          val oldSource = IO.read(source)
        })
      } else {
        None
      }
    }
    streams.log.info("running initial compilation")
    compile
    try {
      streams.log.info("modifying files")
      mods.flatten.foreach { m => IO.write(m.source, m.newSource)}
      streams.log.info("running incremental compilation")
      val (_, an) = compile
      streams.log.info(an.toString)
      streams.log.info("running main class")
      run
    } finally {
      streams.log.info("restoring files")
      mods.flatten.foreach { m => IO.write(m.source, m.oldSource)}
      streams.log.info("compiling them again")
      compile
    }
  }
}
