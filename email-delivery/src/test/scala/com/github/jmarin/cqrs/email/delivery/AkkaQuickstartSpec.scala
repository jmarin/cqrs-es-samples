//#full-example
package com.github.jmarin.cqrs.email.delivery

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.github.jmarin.cqrs.email.delivery.Greeter.Greet
import com.github.jmarin.cqrs.email.delivery.Greeter.Greeted
import org.scalatest.WordSpecLike

//#definition
class AkkaQuickstartSpec extends ScalaTestWithActorTestKit with WordSpecLike {
//#definition

  "A Greeter" must {
    //#test
    "reply to greeted" in {
      val replyProbe = createTestProbe[Greeted]()
      val underTest = spawn(Greeter())
      underTest ! Greet("Santa", replyProbe.ref)
      replyProbe.expectMessage(Greeted("Santa", underTest.ref))
    }
    //#test
  }

}
//#full-example
