package scala.u06.examples.BASICPN

import scala.u06.lab.PetriNetFacade.BasicPN
import scala.u06.modelling.SystemAnalysis

object BasicPn:
  import BasicPN.*
  enum Place:
    case IDLE, TRY, FAIL, SUCC
  import Place.*
  
  def pn = BasicPN[Place](
      >(IDLE) ~~> >(TRY),
      >(TRY) ~~> >(TRY),
      >(TRY) ~~> >(FAIL),
      >(TRY) ~~> >(SUCC),
      >(FAIL) ~~> >(IDLE),
      >(SUCC) ~~> >(SUCC)
    ).toSystem

  

