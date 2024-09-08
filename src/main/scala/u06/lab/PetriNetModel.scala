package scala.u06.lab

import scala.reflect.ClassTag
import scala.u06.modelling.System
import scala.u07.utils.MSet

object PetriNetModel:
  enum Token[T](content: T):
    case Value(content: T, name: String) extends Token[T](content)
    case Empty() extends Token[T](null.asInstanceOf[T])
    override def toString: String = this match
      case Token.Value(content, n) => if n.isEmpty then content.toString else s"$n: $content"
      case Token.Empty() => ""

  import Token.*
  extension [T: ClassTag](t: Token[T])
    def getValue: Option[T] = t match
      case Value(content: T, _) => Some[T](content)
      case _ => Option.empty[T]
  extension (t: Token[?])
    def getName: String = t match
      case Value(_, n) => n
      case Empty() => ""
    def ofName(s:String):Token[?] = t match
      case Value(v, n) => Value(v,s)
      case Empty() => Empty()

  type Box = Iterable[Token[?]]
  type Marking[I,T] = Map[I, Box]
  type Cond = Token[?]
  type Arc = PartialFunction[Box, Box]

  def box():Box = List()
  def box(v:Token[?]*):Box = v.toList
  def box(v:Iterable[Token[?]]):Box = v
  def box(n:Int, v:Token[?]):Box = List.fill(n)(v)
  extension[I](b: List[I])
    def counted: Map[I, Int] = b.groupBy(identity).map((a,n) => a -> n.size)

  trait Transition[I,T]:
    def condition: Map[I, Iterable[Arc]]
    def action: Map[Arc, I]
    def isEnabled(m: Marking[I,T]): Boolean
    def fire(m: Marking[I,T]): Iterable[Marking[I,T]]
    def name: String


  case class PetriNet[I](transitions: Iterable[Transition[I,Token[?]]])

  def apply[I](t: Transition[I,Token[?]]*): PetriNet[I] =
    PetriNet(t.toSet)

  extension [I](p: PetriNet[I])
    def toSystem: System[Marking[I,Token[?]]] = m =>
      (for t <- p.transitions
          if t.isEnabled(m)
      yield t.fire(m).toSeq.distinct).flatten.toSet

  object Transition:

    class TransitionImpl[I, T](override val condition: Map[I, Iterable[Arc]],
                                            override val action: Map[Arc, I],
                                            override val name: String,
                               guard: Box ?=> Boolean) extends Transition[I, T]:
      override def toString: String = name match
        case "" => "T" + (condition.hashCode() + action.hashCode()) % 100
        case _ => name

      // returns all the possible combinations of the elements of the list
      private def combinationList[A](ls: List[List[A]]): List[List[A]] = ls match {
        case Nil => Nil :: Nil
        case head :: tail =>
          val rec = combinationList[A](tail).distinct
          rec.flatMap(r => head.map(t => t :: r))
      }

      // returns all the possible bindings for each arc
      // Each map contains a unique scenario:
      // for each arc, the token on which it can be fired in that scenario
      private def checkBindings(arcs :List[(Arc, Box)]): Iterable[Map[Arc, Token[?]]] =
        case class ArcToken(token: Token[?], box: Box)
        // needed to keep the tokens generated by the arc for a specific a token
        val tokensAfterFire = arcs.map((a, tokens) => a -> tokens.toList.map(t=> ArcToken(t, a(List(t)))))
        val combinations = combinationList(tokensAfterFire.map((a, b) => b.map(a -> _)))
        combinations.filter(p => p.flatMap((a, arcToken) => arcToken.box
              .filter(_.getName.nonEmpty).toSeq).groupBy(_.getName).forall((_, tokens) => tokens.size match
              case 1 => true
              case n => tokens.forall(_ == tokens.head) // all the same
              )).map(perm => perm.map((a, arcToken) =>
          a -> arcToken.token).toMap)

      // for each arc, get the tokens that can fire the arc
      private def getValidArcs(m: Marking[I, T]): List[(Arc, Box)] =
        condition.toList.flatMap((p, arcs) => arcs.map(p -> _)).map((p, a) =>
          val tokens = m.getOrElse(p, box())
          a -> tokens.filter(t => a.isDefinedAt(List(t))))

      // returns only the possible scenarios
      // some scenarios may be not possible e.g.,
      // if two arcs consume the same type of token, the binding it's ok
      // if there is at least one token of that type in the place,
      // but instead there must be two tokens of that type in the place
      private def possibleScenarios(marking: Marking[I,T],
                                    places: Map[I, Iterable[Arc]],
                                    bindings:Iterable[Map[Arc,Token[?]]]):Iterable[Map[Arc,Token[?]]] =
        val invalidScenarios =
          for scenario <- bindings
              p <- places.keys
              neededTokens = places(p).map(a => scenario(a))
              b = marking.getOrElse(p, box())
              // check if the place contains all the needed tokens
              if !MSet.ofList(b.toList).matches(MSet.ofList(neededTokens.toList))
              yield scenario
        bindings.toList diff invalidScenarios.toSeq

      private def fireAllArcs(m:Map[Arc, Token[?]]):Box =
        m.flatMap((a, t) => a(List(t)))


      override def isEnabled(m: Marking[I, T]): Boolean =
        val validArcs = getValidArcs(m)
        val c = checkBindings(validArcs)
        val scenarios = possibleScenarios(m, condition, c)
        // all the guards of the arcs must be true
        
        val validScenarios = scenarios.filter(s => guard(using fireAllArcs(s)))
        // all arcs of the condition have at least one token that can fire it
        validArcs.count((_, b) => b.nonEmpty) == condition.flatMap(_._2).size
        // exists at least one valid scenario
          && validScenarios.nonEmpty


      override def fire(m: Marking[I, T]): Iterable[Marking[I, T]] =
        // assuming that isEnabled is true
        val validArcs = getValidArcs(m)
        val bindings = possibleScenarios(m, condition, checkBindings(validArcs))
        val validScenarios = bindings.filter(s => guard(using s.flatMap((a, t) => a(box(t))))).toSeq
        for s <- LazyList.from(validScenarios)
            afterCondition = condition.map((p, arcs) => p -> arcs.flatMap(a => a(box(s(a)))))
            tokensToRemove = condition.map((p, arcs) => p -> arcs.flatMap(a => box(s(a))))
            newInPlaces = m.map((place, tokens) => condition.isDefinedAt(place) match
            case true => place -> tokens.toSeq.diff(tokensToRemove(place).toSeq)
            case false => place -> tokens)
            distinctTokens = afterCondition.values.flatten
            newOutPlaces = action.toList.map((a, p) =>
            p -> newInPlaces.getOrElse(p, box()).concat(a.applyOrElse(distinctTokens, _ =>
            throw IllegalStateException(s"An out arc of transition ${this.toString} cannot be applied with" +
              s" the tokes taken from InArcs")))).groupMapReduce(_._1)(_._2)((b1,b2) => b1 ++ b2) // can fail
          yield newInPlaces ++ newOutPlaces

    def apply[I,T](): Transition[I,T] = ofMap(Map(), Map(), "", true)
    def apply[I,T](name: String): Transition[I,T] = ofMap(Map(), Map(), name,true)
    def ofMap[I,T](condition: Map[I, Iterable[Arc]], action: Map[Arc, I], name: String,
                   g: Box ?=> Boolean): Transition[I,T] = TransitionImpl(condition, action, name,g)
    def ofList[I, T](condition: Iterable[(I, Arc)], action: Map[Arc, I], name: String,
                     g: Box ?=> Boolean): Transition[I, T] =
      TransitionImpl(condition.groupMapReduce(_._1)(v => Seq(v._2))((x,y) => x ++ y),
                    action,name, g)
