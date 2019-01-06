package service.config;

import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    //Configuration for axon event storage
    @Bean
    public EventStorageEngine eventStorageEngine() {
        return new InMemoryEventStorageEngine();
    }

    // Configurations for amqp
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
        admin.declareExchange(eventsExchangeQueryHandler());
        admin.declareQueue(eventsQueueQueryHandler());
        admin.declareBinding(eventsBindingQueryHandler());
    }
}
