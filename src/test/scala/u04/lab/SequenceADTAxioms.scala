package scala.u04.lab

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}
import u04.datastructures.SequenceCheck.property

import scala.u04.lab.SequenceADT.BasicSequence
import scala.u04.lab.SequenceADT.BasicSequence.*

object SequenceADTAxioms extends Properties("Sequence"):

  given Arbitrary[Sequence[Int]] = Arbitrary {
    Gen.listOf(Gen.choose(0,100)).map(l => BasicSequence.of(l: _*))
  }

  property("mapAxioms") =
    forAll: (seq: Sequence[Int], f: Int => Int) =>
      (seq, f) match
        case (Empty(), f) =>  map(empty)(f) == empty
        case (Cons(h, t), f) => map(cons(h, t))(f) == cons(f(h), map(t)(f))

  property("concatAxiom") =
    forAll: (seq: Sequence[Int], seq2: Sequence[Int]) =>
      (seq, seq2) match
        case (Empty(), l) => concat(empty, l) == l
        case (Cons(h, t), l) => concat(cons(h, t), l) == cons(h, concat(t, l))



