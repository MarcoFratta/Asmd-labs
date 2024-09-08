package scala.u09.lab.examples

import scala.u09.lab.examples.TryEnemyMatrix.M
import scala.u09.lab.gui.Cells.*
import scala.u09.lab.gui.MVC.createGui
import scala.u09.lab.model.BasicMatrix.{Move, Pos}
import scala.u09.lab.model.QMatrix.LearningParams
import scala.util.Random

object EnemiesGui:

  import Move.*
  val w = 15
  val h = 15
  val params = LearningParams(0.9, 0.5, 0.4, 1)
  val obstacles = Set((0,0),(14,14),(0,14),(14,0))
  val model = M(w, h,
    initial = () => (7, 7),
    reward = {case _ => 17.5},
    enemies = Set((7, 7)),
    obstacles = obstacles,
    params = params)
  val q0 = model.qFunction


  @main
  def showEnemyLearning(): Unit =
    val hist = model.makeLearningInstance().learningHist(q0)(10000, 3000)
    createGui(model, 40)(borderGrid
      //.showStartPos((0,0))
      .showObstacles(model.obstacles)
      .showEnemies(model.enemies))(hist)

  @main
  def showEnemyAfterLearning(): Unit =
    val q1 = model.makeLearningInstance().learn(70000, 1000, q0)
    val hist = model.makeLearningInstance().nRuns(q1)(500, 50)(() =>
      Pos(Random.nextInt(w), Random.nextInt(h)))
    println(model.show(s => q1.bestPolicy(s).toString))
    println(model.show(s => "%2.0f".format(q1.vFunction(s))))
    createGui(model, 40)(borderGrid
      .showObstacles(model.obstacles)
      .showEnemies(model.enemies))(hist)
