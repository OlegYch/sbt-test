object B extends App {
  new CachedMap[String, String]() with VersionedCachedMap[String, String, Option]
}