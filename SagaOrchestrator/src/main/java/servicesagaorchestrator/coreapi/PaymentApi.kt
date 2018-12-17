package com.example.demo.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

class DoPaymentCommand(@TargetAggregateIdentifier val user : String, val paymentId : String, val amount : String)
class RefundPaymentCommand(@TargetAggregateIdentifier val user : String, val paymentId : String, val amount : String)

class PaymentDoneEvent(val user : String, val paymentId : String, val amount : String)
class PaymentRefundedEvent(val user : String, val paymentId : String, val amount : String)

class NotEnoughMoneyAccountException() : Exception()