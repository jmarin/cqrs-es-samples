package com.github.jmarin.cqrs.lagom.bank

import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEvent,
  AggregateEventTag,
  PersistentEntity
}
import com.typesafe.config.ConfigFactory

class AccountEntity extends PersistentEntity {

  override type Command = AccountCommand[_]
  override type Event = AccountEvent
  override type State = AccountState

  override def initialState: AccountState = EmptyAccountState()

  val config = ConfigFactory.load()
  val transferFee = config.getDouble("bank.transfer-fee")

  override def behavior: Behavior = {
    case AccountState(_, _) =>
      Actions()
        .onReadOnlyCommand[Get.type, AccountState] {
          case (Get, ctx, state) =>
            ctx.reply(state)
        }
        .onCommand[OpenAccount, OpenAccountDone] {
          case (OpenAccount(account), ctx, state) if !state.open =>
            ctx.thenPersist(AccountOpened(account)) { _ =>
              ctx.reply(OpenAccountDone(account))
            }
          case (OpenAccount(_), ctx, state) if state.open =>
            ctx.invalidCommand("Account is already open")
            ctx.done
        }
        .onCommand[Deposit, DepositDone] {
          case (Deposit(amount), ctx, state) if amount > 0 =>
            ctx.thenPersist(Deposited(amount)) { _ =>
              ctx.reply(DepositDone(amount))
            }
        }
        .onCommand[Withdraw, WithdrawDone] {
          case (Withdraw(amount), ctx, state) =>
            if (amount < state.balance) {
              ctx.thenPersist(Withdrawn(amount)) { _ =>
                ctx.reply(WithdrawDone(amount))
              }
            } else {
              ctx.commandFailed(throw new Exception("Insufficient Funds"))
              ctx.done
            }
        }
        .onCommand[TransferMoney, TransferMoneyDone] {
          case (TransferMoney(to, amount), ctx, state) =>
            if ((amount + transferFee) < state.balance) {
              val evts =
                Seq(
                  TransferFeeDeducted(transferFee),
                  MoneyTransferred(to.id, amount)
                )
              ctx.thenPersistAll(evts: _*) { () =>
                ctx.reply(TransferMoneyDone(to.id, amount))
              }
            } else {
              ctx.commandFailed(throw new Exception("Insufficient Funds"))
              ctx.done
            }

        }
        .onEvent(eventHandler)
  }

  def eventHandler: EventHandler = {
    case (e: AccountEvent, state) => state.applyEvent(e)
  }
}
