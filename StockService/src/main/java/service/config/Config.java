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

    @Autowired
    public void configure(AmqpAdmin admin) {
        admin.declareExchange(eventsExchangeStock());
        admin.declareQueue(eventsQueueStock());
        admin.declareBinding(eventsBindingStock());
    }
}
