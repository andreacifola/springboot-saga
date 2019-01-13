package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class TriggerStockUpdateCommand(@TargetAggregateIdentifier val stockId: String,
                                val articleId: String, val article: String, val quantity: Int)
class StockUpdateTriggeredEvent(val stockId: String, val articleId: String, val article: String, val quantity: Int)

class StockUpdateCommand(@TargetAggregateIdentifier val stockId: String, val articleId: String,
                         val article: String, val quantity: Int, val availability: Int)

class StockUpdatedEvent(val stockId: String, val articleId: String, val article: String, val quantity: Int, val availability: Int)

class AbortStockCommand(@TargetAggregateIdentifier val stockId: String,
                        val articleId: String, val article: String, val quantity: Int)
class StockAbortedEvent(val stockId: String, val articleId: String, val article: String, val quantity: Int)

class TriggerCompensatePaymentCommand(@TargetAggregateIdentifier val stockId: String,
                                      val articleId: String, val article: String, val quantity: Int)
class CompensatePaymentTriggeredEvent(val stockId: String, val articleId: String, val article: String, val quantity: Int)

/* In this case we don't have to add the compensated actions like 'AddAgainArticleCommand'
   as we did instead for Order and Payment because this is the last transaction, so either
   it goes well or it goes wrong; but it is not involved by other future transactions.
 */


class NotEnoughArticlesInTheStockException : Exception()