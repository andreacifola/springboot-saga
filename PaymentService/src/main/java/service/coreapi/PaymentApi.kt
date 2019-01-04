package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class TriggerPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                            val user: String, val paymentId: String, val amount: String)

class PaymentTriggeredEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class DoPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                       val user: String, val paymentId: String, val amount: String)

class PaymentDoneEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class EnableStockUpdateCommand(@TargetAggregateIdentifier val accountId: String, val user: String,
                               val paymentId: String, val amount: String)

class StockUpdateEnabledEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class AbortPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                          val user: String, val paymentId: String, val amount: String)

class PaymentAbortedEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class TriggerCompensateOrderCommand(@TargetAggregateIdentifier val accountId: String,
                                    val user: String, val paymentId: String, val amount: String)

class OrderCompensateTriggeredEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class RefundPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                           val user: String, val paymentId: String, val amount: String)

class PaymentRefundedEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class TriggerEndSagaPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                                   val user: String, val paymentId: String, val amount: String)

class EndSagaPaymentTriggeredEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class EndSagaPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                            val user: String, val paymentId: String, val amount: String)
class SagaPaymentEndedEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)


class NotEnoughMoneyAccountException : Exception()