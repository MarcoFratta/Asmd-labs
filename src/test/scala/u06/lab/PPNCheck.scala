package scala.u06.lab

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}

import scala.u06.examples.PPN.PPn
import scala.u06.lab.PetriNetFacade.PNOperation.token
import scala.u06.lab.{PPNTest, PetriNetFacade}
import scala.u06.utils.MSet
import scala.util.Random
object PPNCheck extends Properties("PriorityNet"):

  import PPn.State
  import PPn.State.*
  import PetriNetFacade.PPN.*
  import scala.u06.modelling.SystemAnalysis.*

  given stateArbitrary:Arbitrary[List[State]] =
    Arbitrary(Gen.listOfN(8, State.values(Random.nextInt(State.values.length))).map(x => List(x*)))

  val net = PPn.net

  property("test always ends in DONE or OP_A") = forAll: (mSet: List[State]) =>
    val res = net.completePathsUpToDepth(marking(mSet*), 100)
    res.forall(x => x.last.getOrElse(OP_B, Seq()).isEmpty &&
                    x.last.getOrElse(RES, Seq()).isEmpty)
