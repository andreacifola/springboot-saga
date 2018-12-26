package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class UpdateStockCommand(@TargetAggregateIdentifier val articleId : String, val article : String, val stockId : String, val quantity : Int)
class StockUpdatedEvent(val articleId : String, val article : String, val stockId : String, val quantity : Int)

class EnableEndSagaCommand(@TargetAggregateIdentifier val articleId: String, val article: String, val stockId: String, val quantity: Int)
class EndSagaEnabledEvent(val articleId: String, val article: String, val stockId: String, val quantity: Int)

class CompensatePaymentCommand(@TargetAggregateIdentifier val articleId: String, val article: String, val stockId: String, val quantity: Int)
class PaymentCompensatedEvent(val articleId: String, val article: String, val stockId: String, val quantity: Int)

class EndSagaStockCommand(@TargetAggregateIdentifier val articleId: String, val article: String, val stockId: String, val quantity: Int)
class StockEndedSagaEvent(val articleId: String, val article: String, val stockId: String, val quantity: Int)

/* In this case we don't have to add the compensated actions like 'AddAgainArticleCommand'
   as we did instead for Order and Payment because this is the last transaction, so either
   it goes well or it goes wrong; but it is not involved by other future transactions.
 */

class NotEnoughArticlesInTheStockException : Exception()