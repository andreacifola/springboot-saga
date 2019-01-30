package service.config;

import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    //Configuration for axon event storage.
    @Bean
    public EventStorageEngine eventStorageEngine() {
        return new InMemoryEventStorageEngine();
    }

    // Configurations for amqp.
    @Bean
    public Exchange eventsExchange() {
        return ExchangeBuilder.fanoutExchange("Try").build();
    }

    @Bean
    public Queue eventsQueueOrder() {
        return QueueBuilder.durable("Order").build();
    }

    @Bean
    public Queue eventsQueuePayment() {
        return QueueBuilder.durable("Payment").build();
    }

    @Bean
    public Queue eventsQueueStock() {
        return QueueBuilder.durable("Stock").build();
    }

    @Bean
    public Queue eventsQueueQueryHandler() {
        return QueueBuilder.durable("QueryHandler").build();
    }

    @Bean
    public Binding eventsBindingOrder() {
        return BindingBuilder.bind(eventsQueueOrder()).to(eventsExchange()).with("orderamqp").noargs();
    }

    @Bean
    public Binding eventsBindingPayment() {
        return BindingBuilder.bind(eventsQueuePayment()).to(eventsExchange()).with("paymentamqp").noargs();
    }

    @Bean
    public Binding eventsBindingStock() {
        return BindingBuilder.bind(eventsQueueStock()).to(eventsExchange()).with("stockamqp").noargs();
    }

    @Bean
    public Binding eventsBindingQueryHandler() {
        return BindingBuilder.bind(eventsQueueQueryHandler()).to(eventsExchange()).with("queryhandleramqp").noargs();
    }

    @Autowired
    public void configure(AmqpAdmin admin) {
        admin.declareExchange(eventsExchange());

        admin.declareQueue(eventsQueueOrder());
        admin.declareQueue(eventsQueuePayment());
        admin.declareQueue(eventsQueueStock());
        admin.declareQueue(eventsQueueQueryHandler());

        admin.declareBinding(eventsBindingOrder());
        admin.declareBinding(eventsBindingPayment());
        admin.declareBinding(eventsBindingStock());
        admin.declareBinding(eventsBindingQueryHandler());
    }
}
