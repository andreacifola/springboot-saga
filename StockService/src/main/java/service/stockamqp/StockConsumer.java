package service.stockamqp;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import service.coreapi.CompensatePaymentCommand;
import service.coreapi.EnableEndSagaCommand;
import service.coreapi.StockUpdatedEvent;
import service.coreapi.UpdateStockCommand;
import service.entities.WareHouseEntity;


@ProcessingGroup("stockEvents")
@RestController
public class StockConsumer {

    private final CommandGateway commandGateway;
    private WareHouseEntity wareHouseEntity = new WareHouseEntity("shirt",  23);

    public StockConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(StockUpdatedEvent event) {

        if (wareHouseEntity.getAvailable() >= event.getQuantity()) {

            System.out.println("\nArticle Id =                                 " + event.getArticleId());
            System.out.println("Number of " + event.getArticle() +
                    "s inside the warehouse =      " + wareHouseEntity.getAvailable());
            System.out.println("Number of " + event.getArticle() +
                    "s ordered =                   " + event.getQuantity());

            wareHouseEntity.setAvailable(wareHouseEntity.getAvailable() - event.getQuantity());

            System.out.println("New number of " + event.getArticle() +
                    "s inside the warehouse =  " + wareHouseEntity.getAvailable() + "\n");

            commandGateway.send(new UpdateStockCommand(event.getArticleId(), event.getArticle(), event.getStockId(), event.getQuantity()));
            commandGateway.send(new EnableEndSagaCommand(event.getArticleId(), event.getArticle(), event.getStockId(), event.getQuantity()));
        } else {
            System.out.println("\nYou don't have enough money in your bank account!\n");
            commandGateway.send(new UpdateStockCommand(event.getArticleId(), event.getArticle(), event.getStockId(), event.getQuantity()));
            commandGateway.send(new CompensatePaymentCommand(event.getArticleId(), event.getArticle(), event.getStockId(), event.getQuantity()));

        }
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
