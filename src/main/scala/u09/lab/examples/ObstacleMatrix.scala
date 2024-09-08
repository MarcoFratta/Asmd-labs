package scala.u09.lab.examples

import scala.u09.lab.model.BasicMatrix.*
import scala.u09.lab.model.{Jumps, Obstacles, OutsideBoundPenalty}

object ObstacleMatrix extends App:

  import scala.u09.lab.model.QMatrix.*
  import scala.u09.lab.model.BasicMatrix.*
  import BasicMatrix.given
  import Move.*

  class MatrixWithObstacle(width: Int,
                            height: Int,
                            initial: () => Pos = () => (0,0),
                            terminal: PartialFunction[Pos, Boolean] ={case _ => false},
                            override val reward: PartialFunction[(Pos, Move), Double],
                           override val jumps: PartialFunction[((Int,Int), Move), (Int,Int)],
                            override val obstacles: Set[(Int,Int)],
                           params: LearningParams = LearningParams.default)
    extends BasicMatrix(width, height, initial, terminal, reward,params)
    with Jumps[Pos,Move](jumps)
    with Obstacles[Pos, Move](obstacles)
    with OutsideBoundPenalty[Pos, Move](width, height)


  @main
    def corridor():Unit =
      val rl = MatrixWithObstacle(7, 3, reward = {
        case (Pos(6,0), RIGHT) => 100
        case _ => 0
        }, jumps = {
        case ((6,0), RIGHT) => (0, 0)
      }, obstacles = Set((1, 0), (1, 1),
        (3, 2), (3,1),
        (5,0), (5,1)
      ))

      val q0 = rl.qFunction
      val q1 = rl.makeLearningInstance().learn(10000, 200, q0)
      println(rl.show(s => "%2.0f".format(q0.vFunction(s))))
      println(rl.show(s => "%2.0f".format(q1.vFunction(s))))
      println(rl.show(s => q1.bestPolicy(s).toString))

