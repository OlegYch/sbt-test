import sbt._

object Build extends sbt.Build {
  lazy val testIncrementalCompile = TaskKey[Unit]("testIncrementalCompile")

  lazy val root = Project("sbt-test", file(".")).settings(
    testIncrementalCompile <<= (Keys.state, Keys.streams) map tic
  )

  def tic(state: State, streams: Keys.TaskStreams): Unit = {
    val extracted = Project.extract(state)
    val compileKey = Keys.compile in Compile
    def compile = extracted.runTask(compileKey, state)
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
    compile
    try {
      mods.flatten.foreach { m => IO.write(m.source, m.newSource)}
      val (_, an) = compile
      streams.log.info(an.toString)
    } finally {
      mods.flatten.foreach { m => IO.write(m.source, m.oldSource)}
      compile
    }
  }
}
