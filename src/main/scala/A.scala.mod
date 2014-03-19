trait BaseCachedMap[K, V, M[_]] {
  def get: Int
}
trait VersionedCachedMap[K <: AnyRef, V, M[_]] extends BaseCachedMap[K, V, M] {
  private val a = new Object
  abstract override def get:Int = {List().foreach{_ => println(a); super.get}; 1}
}
