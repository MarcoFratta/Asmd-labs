package scala.u04.lab

import scala.annotation.tailrec

object SequenceADT:
  trait SequenceType:
    type Sequence[A]
    def map[A, B](seq: Sequence[A])(f: A => B): Sequence[B]
    def filter[A](seq: Sequence[A])(predicate: A => Boolean): Sequence[A]
    def flatMap[A, B](seq: Sequence[A])(f: A => Sequence[B]): Sequence[B]
    def foldLeft[A, B](initial: B)(seq: Sequence[A])(f: (B, A) => B): B
    def foldRight[A, B](initial: B)(seq: Sequence[A])(f: (A, B) => B): B
    def reduce[A](seq: Sequence[A])(f: (A, A) => A): A
    def concat[A](seq1: Sequence[A], seq2: Sequence[A]): Sequence[A]



  object BasicSequence extends SequenceType:
    private enum Seq[A]:
      case Cons(head: A, tail: Sequence[A])
      case Nil()

    opaque type Sequence[A] = Seq[A]
    
    object Empty:
      def unapply[A](seq: Sequence[A]): Boolean = seq match
        case Seq.Cons(_, _) => false
        case Seq.Nil() => true
    object Cons:
      def unapply[A](seq: Sequence[A]): Option[(A, Sequence[A])] = seq match
        case Seq.Cons(head, tail) => Some((head, tail))
        case Seq.Nil() => None

    def of[A](values: A*): Sequence[A] = values.foldLeft(empty[A])((acc, el) => Seq.Cons(el, acc))
    def empty[A]: Sequence[A] = Seq.Nil()

    def Nil[A](seq: Sequence[A]): Option[Sequence[A]] = seq match
      case Seq.Cons(head, tail) => None
      case Seq.Nil() => Some(Seq.Nil())

    def cons[A](h:A, t: Sequence[A]): Sequence[A] = Seq.Cons(h, t)



    override def map[A, B](seq: Sequence[A])(f: A => B): Sequence[B] = seq match
      case Seq.Cons(head, tail) => Seq.Cons(f(head), map(tail)(f))
      case Seq.Nil() => empty
    override def filter[A](seq: Sequence[A])(predicate: A => Boolean): Sequence[A] = seq match
      case Seq.Cons(head, tail) if predicate(head) => Seq.Cons(head, filter(tail)(predicate))
      case Seq.Cons(_, tail) => filter(tail)(predicate)
      case Seq.Nil() => empty
    override def flatMap[A, B](seq: Sequence[A])(f: A => Sequence[B]): Sequence[B] = seq match
      case Seq.Cons(head, tail) => f(head) match
        case Seq.Cons(h, t) => Seq.Cons(h, flatMap(tail)(f))
        case Seq.Nil() => flatMap(tail)(f)
      case Seq.Nil() => empty
    @tailrec
    override def foldLeft[A, B](initial: B)(seq: Sequence[A])(f: (B, A) => B): B = seq match
      case Seq.Cons(head, tail) => foldLeft(f(initial, head))(tail)(f)
      case Seq.Nil() => initial

    override def foldRight[A, B](initial: B)(seq: Sequence[A])(f: (A, B) => B): B = seq match
      case Seq.Cons(head, tail) => f(head, foldRight(initial)(tail)(f))
      case Seq.Nil() => initial
    override def reduce[A](seq: Sequence[A])(f: (A, A) => A): A = seq match
      case Seq.Cons(head, tail) => foldLeft(head)(tail)(f)
      case Seq.Nil() => throw new IllegalArgumentException("Empty sequence")

    override def concat[A](seq1: Sequence[A], seq2: Sequence[A]): Sequence[A] =
      seq1 match
        case Seq.Cons(head, tail) => Seq.Cons(head, concat(tail, seq2))
        case Seq.Nil() => seq2




