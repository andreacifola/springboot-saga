package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class StartSagaCommand(@TargetAggregateIdentifier val orderId: String, val user: String,
                       val article: String, val quantity: Int, val price: String)

class SagaStartedEvent(val orderId: String, val user: String,
                       val article: String, val quantity: Int, val price: String)

class TriggerPaymentCommand(@TargetAggregateIdentifier val accountId: String,
                            val user: String, val paymentId: String, val amount: String)

class PaymentTriggeredEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class TriggerStockUpdateCommand(@TargetAggregateIdentifier val articleId: String, val article: String, val stockId: String, val quantity: Int)
class StockUpdateTriggeredEvent(val articleId: String, val article: String, val stockId: String, val quantity: Int)