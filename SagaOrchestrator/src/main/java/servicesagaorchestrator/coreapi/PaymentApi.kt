package servicesagaorchestrator.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class DoPaymentCommand(@TargetAggregateIdentifier val accountId : String, val user : String, val paymentId : String, val amount : String)
class RefundPaymentCommand(@TargetAggregateIdentifier val accountId : String, val user : String, val paymentId : String, val amount : String)

class PaymentDoneEvent(val accountId : String, val user : String, val paymentId : String, val amount : String)
class PaymentRefundedEvent(val accountId : String, val user : String, val paymentId : String, val amount : String)

class NotEnoughMoneyAccountException : Exception()