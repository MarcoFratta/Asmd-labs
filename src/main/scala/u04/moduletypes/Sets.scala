package scala.u04.moduletypes

import u04.datastructures.Sequences.*
import Sequence.*

object Sets:
  
  trait SetADT:
    type Set[A]
    def fromSequence[A](s: Sequence[A]): Set[A]
    def union[A](s1: Set[A], s2: Set[A]): Set[A]
    def intersection[A](s1: Set[A], s2: Set[A]): Set[A]
    extension [A](s: Set[A]) 
      def contains(a: A): Boolean
      def remove(a: A): Set[A]
      def toSequence(): Sequence[A]
    

  object BasicSetADT extends SetADT:

    opaque type Set[A] = Sequence[A]

    def fromSequence[A](s: Sequence[A]): Set[A] = s match
      case Cons(h, t) => Cons(h, fromSequence(t.remove(h)))
      case Nil() => Nil()
    
    def union[A](s1: Set[A], s2: Set[A]): Set[A] = s2 match
      case Cons(h, t) => Cons(h, union(s1.remove(h), t))
      case Nil() => s1
    
    def intersection[A](s1: Set[A], s2: Set[A]): Set[A] = s1 match
      case Cons(h, t) if s2.contains(h) => Cons(h, intersection(t, s2.remove(h)))
      case Cons(_, t) => intersection(t, s2)
      case Nil() => Nil()
    
    extension [A](s: Set[A]) 
      def remove(a: A): Set[A] = s.filter(_ != a)  
      def contains(a: A): Boolean = s match
        case Cons(h, t) if h == a => true
        case Cons(_, t) => t.contains(a)
        case Nil() => false
      def toSequence(): Sequence[A] = s

@main def trySetADTModule =
  import Sets.* 
  val setADT: SetADT = BasicSetADT
  import setADT.*

  val s1: Set[Int] = fromSequence(Cons(10, Cons(20, Cons(10, Cons(30, Nil())))))
  val s2: Set[Int] = fromSequence(Cons(10, Cons(11, Nil())))
  // val s3: Set[Int] = Cons(10, Nil()) // because Set is defined opaque
  println(s1.toSequence()) // (10, 20, 30)
  println(s2.toSequence()) // (10, 11)
  println(union(s1, s2).toSequence()) // (10, 20, 30, 11)
  println(intersection(s1, s2).toSequence()) // (10)