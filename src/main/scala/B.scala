/**
  */
trait B /*extends A */{
  def f1 {new A {}}
  def f2 { new C {}}
//  def f = new A {}.f
}
