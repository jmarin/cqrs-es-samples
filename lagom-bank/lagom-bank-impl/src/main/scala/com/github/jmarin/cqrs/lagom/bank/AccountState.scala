package com.github.jmarin.cqrs.lagom.bank


case class AccountState(open: Boolean = false, balance: BigDecimal)