package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class DoPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                       val user: String, val paymentId: String, val amount: String)

class RefundPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                           val user: String, val paymentId: String, val amount: String)

class EnableStockUpdateCommand(@TargetAggregateIdentifier val accountId: String,
                               val user: String, val paymentId: String, val amount: String)

class CompensateOrderCommand(@TargetAggregateIdentifier val accountId: String,
                             val user: String, val paymentId: String, val amount: String)

class EnablePaymentCommand(@TargetAggregateIdentifier val accountId: String, val user: String,
                           val paymentId: String, val amount: String)

class PaymentDoneEvent(val accountId : String, val user : String, val paymentId : String, val amount : String)
class PaymentRefundedEvent(val accountId : String, val user : String, val paymentId : String, val amount : String)
class PaymentEnabledEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)
class StockUpdateEnabledEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)
class OrderCompensatedEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class NotEnoughMoneyAccountException : Exception()