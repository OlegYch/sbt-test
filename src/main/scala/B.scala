case class CachedMap[K, V]() extends BaseCachedMap[K, V, Option] {
  def get: Int = 1
}
class C() extends CachedMap[String, String]() with VersionedCachedMap[String, String, Option]
object B extends App {
  new C().get
}
