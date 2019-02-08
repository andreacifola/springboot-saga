package service.config;

import com.mongodb.MongoClient;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.mongo.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    //Configuration for axon event storage with MongoDB.
    @Bean
    public EventStorageEngine eventStore(MongoClient client) {
        return new MongoEventStorageEngine(new DefaultMongoTemplate(client, "stockevents"));
    }

    // Configurations for amqp (Saga).
    @Bean
    public Exchange eventsExchangeSaga() {
        return ExchangeBuilder.fanoutExchange("Try").build();
    }

    @Bean
    public Queue eventsQueueOrderSaga() {
        return QueueBuilder.durable("Order").build();
    }

    @Bean
    public Queue eventsQueuePaymentSaga() {
        return QueueBuilder.durable("Payment").build();
    }

    @Bean
    public Queue eventsQueueStockSaga() {
        return QueueBuilder.durable("Stock").build();
    }

    @Bean
    public Queue eventsQueueQueryHandlerSaga() {
        return QueueBuilder.durable("QueryHandler").build();
    }

    @Bean
    public Binding eventsBindingOrderSaga() {
        return BindingBuilder.bind(eventsQueueOrderSaga()).to(eventsExchangeSaga()).with("orderamqp").noargs();
    }

    @Bean
    public Binding eventsBindingPaymentSaga() {
        return BindingBuilder.bind(eventsQueuePaymentSaga()).to(eventsExchangeSaga()).with("paymentamqp").noargs();
    }

    @Bean
    public Binding eventsBindingStockSaga() {
        return BindingBuilder.bind(eventsQueueStockSaga()).to(eventsExchangeSaga()).with("stockamqp").noargs();
    }

    @Bean
    public Binding eventsBindingQueryHandlerSaga() {
        return BindingBuilder.bind(eventsQueueQueryHandlerSaga()).to(eventsExchangeSaga()).with("queryhandleramqp").noargs();
    }

    // Configurations for amqp (OrderService).
    @Bean
    public Exchange eventsExchangeOrder() {
        return ExchangeBuilder.fanoutExchange("OrderEvents").build();
    }

    @Bean
    public Queue eventsQueueOrder() {
        return QueueBuilder.durable("OrderSaga").build();
    }

    @Bean
    public Binding eventsBindingOrder() {
        return BindingBuilder.bind(eventsQueueOrder()).to(eventsExchangeOrder()).with("order").noargs();
    }

    // Configurations for amqp (PaymentService).
    @Bean
    public Exchange eventsExchangePayment() {
        return ExchangeBuilder.fanoutExchange("PaymentEvents").build();
    }

    @Bean
    public Queue eventsQueuePayment() {
        return QueueBuilder.durable("PaymentSaga").build();
    }

    @Bean
    public Binding eventsBindingPayment() {
        return BindingBuilder.bind(eventsQueuePayment()).to(eventsExchangePayment()).with("payment").noargs();
    }

    // Configurations for amqp (StockService).
    @Bean
    public Exchange eventsExchangeStock() {
        return ExchangeBuilder.fanoutExchange("StockEvents").build();
    }

    @Bean
    public Queue eventsQueueStock() {
        return QueueBuilder.durable("StockSaga").build();
    }

    @Bean
    public Binding eventsBindingStock() {
        return BindingBuilder.bind(eventsQueueStock()).to(eventsExchangeStock()).with("stock").noargs();
    }

    // Configurations for amqp (QueryHandler).
    @Bean
    public Exchange eventsExchangeQueryHandler() {
        return ExchangeBuilder.fanoutExchange("QueryEventsEvents").build();
    }

    @Bean
    public Queue eventsQueueQueryHandler() {
        return QueueBuilder.durable("QueryHandlerSaga").build();
    }

    @Bean
    public Binding eventsBindingQueryHandler() {
        return BindingBuilder.bind(eventsQueueQueryHandler()).to(eventsExchangeQueryHandler()).with("queryhandler").noargs();
    }

    @Autowired
    public void configure(AmqpAdmin admin) {
        admin.declareExchange(eventsExchangeOrder());
        admin.declareExchange(eventsExchangePayment());
        admin.declareExchange(eventsExchangeStock());
        admin.declareExchange(eventsExchangeQueryHandler());
        admin.declareExchange(eventsExchangeSaga());

        admin.declareQueue(eventsQueueOrder());
        admin.declareQueue(eventsQueuePayment());
        admin.declareQueue(eventsQueueStock());
        admin.declareQueue(eventsQueueQueryHandler());
        admin.declareQueue(eventsQueueOrderSaga());
        admin.declareQueue(eventsQueuePaymentSaga());
        admin.declareQueue(eventsQueueStockSaga());
        admin.declareQueue(eventsQueueQueryHandlerSaga());

        admin.declareBinding(eventsBindingOrder());
        admin.declareBinding(eventsBindingPayment());
        admin.declareBinding(eventsBindingStock());
        admin.declareBinding(eventsBindingQueryHandler());
        admin.declareBinding(eventsBindingOrderSaga());
        admin.declareBinding(eventsBindingPaymentSaga());
        admin.declareBinding(eventsBindingStockSaga());
        admin.declareBinding(eventsBindingQueryHandlerSaga());
    }
}
