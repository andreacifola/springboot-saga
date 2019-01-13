package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class QueryHandlerSaveOrderCommand(@TargetAggregateIdentifier val orderId: String,
                                   val user: String, val article: String, val quantity: Int, val price: String)
class QueryHandlerOrderSavedEvent(val orderId: String, val user: String,
                                  val article: String, val quantity: Int, val price: String)

class QueryHandlerSaveStockCommand(@TargetAggregateIdentifier val stockId: String, val articleId: String,
                                   val article: String, val quantity: Int, val availability: Int)

class QueryHandlerStockSavedEvent(val stockId: String, val articleId: String, val article: String, val quantity: Int, val availability: Int)

//TODO aggiungere comandi ed eventi per le cancellazioni dopo le azioni compensative