package com.github.jmarin.cqrs.lagom.bank

import org.scalacheck.{Arbitrary, Gen}

object AccountGenerator {

  implicit val arbAccount: Arbitrary[Account] = Arbitrary(genAccount)
  implicit val arbAccountState: Arbitrary[AccountState] = Arbitrary(
    genAccountState
  )

  private def genAccount: Gen[Account] =
    for {
      id <- Gen.alphaNumStr
      amount <- Gen.choose(0, 1000)
    } yield Account(id, amount)

  private def genAccountState: Gen[AccountState] =
    for {
      isOpen <- Gen.oneOf(true, false)
      balance <- Gen.choose(0, 1000)
    } yield AccountState(isOpen, balance)

}
