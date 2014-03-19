trait BaseCachedMap[K, V, M[_]] {
  def get: Int
}
trait VersionedCachedMap[K <: AnyRef, V, M[_]] extends BaseCachedMap[K, V, M] {

}
case class CachedMap[K, V]() extends BaseCachedMap[K, V, Option] {
  def get: Int = 1
}