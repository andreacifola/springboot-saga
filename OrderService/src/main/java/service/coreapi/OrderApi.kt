package service.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier


class StartSagaCommand(@TargetAggregateIdentifier val orderId: String, val user: String,
                       val article: String, val quantity: Int, val price: String)

class SagaStartedEvent(val orderId: String, val user: String,
                       val article: String, val quantity: Int, val price: String)

class CreateOrderCommand(@TargetAggregateIdentifier val orderId : String, val user : String,
                         val article : String, val quantity : Int, val price : String)
class OrderCreatedEvent(val orderId : String, val user : String,
                        val article : String, val quantity : Int, val price : String)

class DeleteOrderCommand(@TargetAggregateIdentifier val orderId : String, val user : String,
                         val article : String, val quantity : Int, val price : String)
class OrderDeletedEvent(val orderId : String, val user : String,
                        val article : String, val quantity : Int, val price : String)

class EndSagaOrderCommand(@TargetAggregateIdentifier val orderId: String, val user: String,
                          val article: String, val quantity: Int, val price: String)

class SagaEndedOrderEvent(val orderId: String, val user: String,
                          val article: String, val quantity: Int, val price: String)

class EndTheSagaCommand(@TargetAggregateIdentifier val orderId: String, val user: String,
                        val article: String, val quantity: Int, val price: String)

class TheSagaEndedEvent(val orderId: String, val user: String,
                        val article : String, val quantity : Int, val price : String)