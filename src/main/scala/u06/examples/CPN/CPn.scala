package scala.u06.examples.CPN

import scala.u06.lab.PetriNetModel
import scala.u06.lab.PetriNetModel.PetriNet
import scala.u06.lab.PetriNetFacade.CPN

object CPn:
  enum Place:
    case SEND, RECEIVED, NS, NR,
    A, B, C, D

  import Place.*
  import scala.u06.lab.PetriNetFacade.CPN.*

  type Packet = (Int, String)
  type Counter = Int

  given stringSum: Composable[String] = _ + _

  // Set how a packet should be expanded to its components
  private val packet = ~+((p: Packet) => >(p._1 -> "n", p._2 -> "p"))

  def comProtocol: PetriNet[Place] = CPN[Place](
    >(SEND <-- packet,
      NS <-- <>[Int]("n")) ~~> "Send packet" ~~> >(
      <>[Packet] --> A,
      <>[Packet] --> SEND,
      <>[Int]("n") --> NS),
    >(A <-- <>[Packet]) ~~> "Transmit packet" ~~> >(<>[Packet] --> B),
    >(B <-- packet,
      NR <-- <>[Int]("n"),
      RECEIVED <-- <>[String]("str")) ~~> "Receive packet" ~~> >(
      :~:[Int]("n")(_ + 1) --> C,
      :~:[Int]("n")(_ + 1) --> NR,
      >>[String](<>[String]("str"), <>[String]("p")) % (_ => "str") --> RECEIVED),
    >(C <-- <>[Int]) ~~> "Transmit ack" ~~> >(<>[Int] --> D),
    >(D <-- <>[Int], NS <-- :~[Int]("k")) ~~> "Receive ack" ~~> >(<>[Int]("n") --> NS),
  )
