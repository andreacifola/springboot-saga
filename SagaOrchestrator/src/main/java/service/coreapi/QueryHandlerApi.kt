package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class QueryHandlerSaveOrderCommand(@TargetAggregateIdentifier val orderId: String, val user: String,
                                   val article: String, val quantity: Int, val price: String)

class QueryHandlerOrderSavedEvent(val orderId: String, val user: String,
                                  val article: String, val quantity: Int, val price: String)

class QueryHandlerSavePaymentCommand(@TargetAggregateIdentifier val accountId: String,
                                     val user: String, val paymentId: String, val amount: String)

class QueryHandlerPaymentSavedEvent(val accountId: String, val user: String, val paymentId: String, val amount: String)

class QueryHandlerSaveStockCommand(@TargetAggregateIdentifier val articleId: String, val article: String, val stockId: String, val quantity: Int)
class QueryHandlerStockSavedEvent(val articleId: String, val article: String, val stockId: String, val quantity: Int)