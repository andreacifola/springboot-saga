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

    //Configuration for axon event storage with MongoDB
    @Bean
    public EventStorageEngine eventStore(MongoClient client) {
        return new MongoEventStorageEngine(new DefaultMongoTemplate(client, "stockevents"));
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
