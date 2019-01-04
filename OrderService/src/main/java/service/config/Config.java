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
        return new MongoEventStorageEngine(new DefaultMongoTemplate(client, "orderevents"));
    }

    // Configurations for amqp
    @Bean
    public Exchange eventsExchange() {
        return ExchangeBuilder.fanoutExchange("OrderEvents").build();
    }

    @Bean
    public Queue eventsQueueOrder() {
        return QueueBuilder.durable("OrderSaga").build();
    }

    @Bean
    public Binding eventsBindingOrder() {
        return BindingBuilder.bind(eventsQueueOrder()).to(eventsExchange()).with("order").noargs();
    }

    @Autowired
    public void configure(AmqpAdmin admin) {
        admin.declareExchange(eventsExchange());
        admin.declareQueue(eventsQueueOrder());
        admin.declareBinding(eventsBindingOrder());
    }
}
