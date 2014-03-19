trait BaseCachedMap[K, V, M[_]] {
  def get: Int
}
trait VersionedCachedMap[K <: AnyRef, V, M[_]] extends BaseCachedMap[K, V, M] {

}
