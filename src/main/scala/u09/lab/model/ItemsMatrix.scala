package scala.u09.lab.model

import BasicMatrix.*
import QMatrix.*
import scala.u09.lab.model.Utils.*
import scala.util.Random

object ItemsMatrix:
  import Move.*
  

  case class PosWithItems(override val x: Int,override val y: Int,
                          items: Set[(Int,Int)]) extends Pos2d[PosWithItems]:
    override def toString = s"($x,$y, items: ${items.mkString("[",",","]")})"
    override def of(p: (Int, Int)): PosWithItems = PosWithItems(p._1, p._2, this.items)

  object PosWithItems:
    def at(p: PosWithItems): Option[(Int, Int, Set[(Int,Int)])] = Some(p.x, p.y, p.items)

  type State = PosWithItems

  given Conversion[(Int,Int, Set[(Int,Int)]), PosWithItems] = p => PosWithItems(p._1, p._2, p._3)

  case class ItemsMatrix(override val width: Int,
                         override val height: Int,
                         override val initial: () => State = () => (0, 0, Set()),
                         override val terminal: PartialFunction[State, Boolean] = {case _ => false},
                         items: Map[Int, (Int,Int)] = Map(),
                         releasePos: Set[(Int,Int)] = Set(),
                         rewardPerItem: Double = 1,
                         rewardPerRelease: Double = 10,
                         override val reward: PartialFunction[(State, Move), Double],
                         override val params: LearningParams) extends Field[State, Move]:
    override type Action = Move
    override type State = PosWithItems

    override def nextPos(p: (Int,Int), m: Move): (Int,Int) = m match
      case LEFT => (0 max p._1 - 1, p._2)
      case RIGHT => (width min p._1 + 1, p._2)
      case UP => (p._1, 0 max p._2 - 1)
      case DOWN => (p._1, p._2 + 1 min (height - 1))

    override def qEnvironment(): Environment = (s: State, a: Action) =>
      (withItems(reward)((s, a)), nextState(s, a))

    override def nextState(s: State, a: Action): State =
      s.of(nextPos((s.x,s.y), a)) match
        case n if hasCorrectItem(n) => (n.x, n.y, n.items + ((n.x,n.y)))
        case n if canRelease(n) => (n.x, n.y, Set()) // empty the set
        case n => n

    private def withItems(r: PartialFunction[(State, Action), Double]):
      PartialFunction[(State, Action), Double] = {
        case (s, a) if hasCorrectItem(->(s, a))  => rewardPerItem
        case (s, a) if alreadyPicked(->(s, a)) => Double.MinValue
        case (s, a) if cantRelease(->(s, a)) => Double.MinValue
        case (s, a) if canRelease(->(s, a)) => rewardPerRelease * s.items.size
        case (s, a) => r.apply((s, a))
      }

    private def cantRelease(s: State): Boolean =
      releasePos.contains((s.x,s.y)) && s.items != items.values.toSet
    private def alreadyPicked(s: State): Boolean =
      s.items.contains(s.pos)
    private def hasCorrectItem(s: State): Boolean =
      items.applyOrElse(s.items.size, _ => (-1,-1)) == s.pos
    private def canRelease(s: State): Boolean =
      releasePos.contains((s.x, s.y)) && s.items == items.values.toSet

    private def allItemsCombinations(l: List[(Int,Int)]): Set[Set[(Int,Int)]] = l.size match
      case 0 => Set()
      case _ => Set(l.toSet).union(allItemsCombinations(l.dropRight(1)))

    override def qFunction: Q = QFunction(Move.values.toSet, params.v0, terminal)
    override def show[E](v: State => String): String =
      (for s <- allItemsCombinations(items.toList.sortBy(_._1).map(_._2)).+(Set())
        yield "\n Picked up -> " + s.mkString("[",",","]") + "\n" +
          (for row <- 0 until height
        col <- 0 until width
          yield ((col, row, s) match
          case s if hasCorrectItem(s) =>"I "
          case s if canRelease(s) => "R "
          case s => v(s)) +
        (if (col == height - 1) "\n" else "\t")).mkString("")).mkString("\n")


