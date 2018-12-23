package service.stockamqp;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import service.coreapi.StockUpdatedEvent;
import service.entities.WareHouseEntity;


@ProcessingGroup("stockEvents")
@RestController
public class StockConsumer {

    private WareHouseEntity wareHouseEntity = new WareHouseEntity("shirt",  23);

    @EventHandler
    public Integer on(StockUpdatedEvent event) {

        System.out.println("\nArticle Id =                                 " + event.getArticleId());
        System.out.println("Number of " + event.getArticle() +
                "s inside the warehouse =      " + wareHouseEntity.getAvailable());
        System.out.println("Number of " + event.getArticle() +
                "s ordered =                   " + event.getQuantity());

        wareHouseEntity.setAvailable(wareHouseEntity.getAvailable() - event.getQuantity());

        System.out.println("New number of " + event.getArticle() +
                "s inside the warehouse =  " + wareHouseEntity.getAvailable() + "\n");
        return wareHouseEntity.getAvailable();
    }

    @Bean
    public SpringAMQPMessageSource stockQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "Stock")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }
}
