package scala.u09.lab.examples

import scala.u09.lab.model.BasicMatrix.*
import scala.u09.lab.model.{Jumps, Obstacles, OutsideBoundPenalty}

object TryBasicMatrix extends App:

  import scala.u09.lab.model.QMatrix.*
  import scala.u09.lab.model.BasicMatrix.*
  import BasicMatrix.given
  import Move.*



  @main
  def basicMatrix():Unit =
    val rl = BasicMatrix( width = 5,
      height = 5,
      initial = () => (0,0),
      terminal = {case _=> false},
      reward = { 
        case (Pos(1,0),DOWN) => 10; 
      case (Pos(3,0),DOWN) => 5; 
      case _ => 0},
      params = LearningParams.default
    )

    val q0 = rl.qFunction
    val q1 = rl.makeLearningInstance().learn(10000, 200, q0)
    println(rl.show(s => "%2.0f".format(q0.vFunction(s))))
    println(rl.show(s => "%2.0f".format(q1.vFunction(s))))
    println(rl.show(s => q1.bestPolicy(s).toString))

