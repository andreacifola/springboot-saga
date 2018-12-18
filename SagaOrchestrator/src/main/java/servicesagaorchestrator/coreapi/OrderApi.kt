package servicesagaorchestrator.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier


class CreateOrderCommand(@TargetAggregateIdentifier val orderId : String, val user : String,
                         val article : String, val quantity : Int, val price : String)
class OrderCreatedEvent(val orderId : String, val user : String,
                        val article : String, val quantity : Int, val price : String)

class DeleteOrderCommand(@TargetAggregateIdentifier val orderId : String, val user : String,
                         val article : String, val quantity : Int, val price : String)
class OrderDeletedEvent(val orderId : String, val user : String,
                        val article : String, val quantity : Int, val price : String)