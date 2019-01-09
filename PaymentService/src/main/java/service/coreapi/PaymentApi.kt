package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class TriggerPaymentCommand(@TargetAggregateIdentifier val paymentId: String, val user: String, val amount: String)
class PaymentTriggeredEvent(val paymentId: String, val user: String, val amount: String)

class DoPaymentCommand(@TargetAggregateIdentifier val paymentId: String, val user: String, val amount: String)
class PaymentDoneEvent(val paymentId: String, val user: String, val amount: String)

class AbortPaymentCommand(@TargetAggregateIdentifier val paymentId: String, val user: String, val amount: String)
class PaymentAbortedEvent(val paymentId: String, val user: String, val amount: String)

class TriggerCompensateOrderCommand(@TargetAggregateIdentifier val paymentId: String, val user: String, val amount: String)
class OrderCompensateTriggeredEvent(val paymentId: String, val user: String, val amount: String)

class EnableStockUpdateCommand(@TargetAggregateIdentifier val paymentId: String, val user: String, val amount: String)
class StockUpdateEnabledEvent(val paymentId: String, val user: String, val amount: String)

class RefundPaymentCommand(@TargetAggregateIdentifier val paymentId: String, val user: String, val amount: String)
class PaymentRefundedEvent(val paymentId: String, val user: String, val amount: String)

class TriggerEndSagaPaymentCommand(@TargetAggregateIdentifier val paymentId: String, val user: String, val amount: String)
class EndSagaPaymentTriggeredEvent(val paymentId: String, val user: String, val amount: String)

class EndSagaPaymentCommand(@TargetAggregateIdentifier val paymentId: String, val user: String, val amount: String)
class SagaPaymentEndedEvent(val paymentId: String, val user: String, val amount: String)


class NotEnoughMoneyAccountException : Exception()