package scala.u09.lab.examples

import scala.u09.lab.model.BasicMatrix.*
import scala.u09.lab.model.ItemsMatrix.{ItemsMatrix, PosWithItems}
import scala.u09.lab.model.{Enemies, Jumps, Obstacles, OutsideBoundPenalty}
import scala.u09.lab.model.QMatrix.LearningParams


object TryItemMatrix extends App:

  import Move.*

  type State = PosWithItems

  class M(width: Int,
          height: Int,
          initial: () => State,
          terminal: PartialFunction[State, Boolean] = {case _ => false},
          override val reward: PartialFunction[(State, Move), Double] = {case _ => 0},
          jumps: PartialFunction[((Int,Int), Move), (Int,Int)] = Map.empty,
          releasePos: Set[(Int,Int)] = Set(),
          items: Map[Int,(Int,Int)] = Map(),
          rewardPerItem: Double = 1,
          rewardPerRelease: Double = 10,
          obstacles: Set[(Int,Int)] = Set(),
          override val enemies: Set[(Int, Int)] = Set(),
          val enemyPenalty:Double = 0,
          params: LearningParams = LearningParams.default)
    extends ItemsMatrix(width, height, initial, terminal,
    items, releasePos, rewardPerItem, rewardPerRelease, reward, params)
    with Obstacles[State, Move](obstacles)
    with Jumps[State, Move](jumps)
    with OutsideBoundPenalty[State, Move](width, height)
    with Enemies[State, Move](enemies, enemyPenalty)


  @main
  def withObstaclesAndJumps(): Unit =
    val items = Map(0 -> (4,4))
    val obstacles = Set((3,2),(3,1),(3,0),(3,3),(4,3))
    val rl = M(5, 5,
      initial =  () => (0,0,Set()),
      terminal = {
        case x if obstacles.contains((x.x,x.y)) =>
          println("Obstacle hit")
          true
        case _ => false},
      reward = {case _ => 0},
      jumps = {
        case ((2, 0), RIGHT) => (4, 0)
      },
      obstacles = obstacles,
      releasePos = Set((4,2)),
      items = items,
      rewardPerItem = 0,
      rewardPerRelease = 1)

    val q0 = rl.qFunction
    println(rl.show(s => "%2.1f".format(q0.vFunction(s))))
    val q1 = rl.makeLearningInstance().learn(10000, 100, q0)
    println(rl.show(s => "%2.1f".format(q1.vFunction(s))))
    println(rl.show(rl.printJumps(q1)))

  @main
  def multipleItems():Unit =
    val params = LearningParams(0.9, 0.5, 0.45, 0)
    val items = Map(0 -> (5,0), 1 ->  (0,5))
    val rl = M(6, 6,
      initial = () => (0, 0, Set()),
      terminal = {case _ => false},
      releasePos = Set((5, 5)),
      items = items,
      rewardPerItem = 0,
      rewardPerRelease = 1)

    val q0 = rl.qFunction
    println(rl.show(s => "%2.1f".format(q0.vFunction(s))))
    val q1 = rl.makeLearningInstance().learn(10000, 500, q0)
    println(rl.show(s => "%2.1f".format(q1.vFunction(s))))
    println(rl.show(s => q1.bestPolicy(s).toString))
