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
        return new MongoEventStorageEngine(new DefaultMongoTemplate(client, "paymentevents"));
    }

    // Configurations for amqp
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

    @Autowired
    public void configure(AmqpAdmin admin) {
        admin.declareExchange(eventsExchangePayment());
        admin.declareQueue(eventsQueuePayment());
        admin.declareBinding(eventsBindingPayment());
    }
}
