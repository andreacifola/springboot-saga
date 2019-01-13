package service.stockamqp;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import service.coreapi.AbortStockCommand;
import service.coreapi.StockUpdateTriggeredEvent;
import service.coreapi.TriggerStockUpdateCommand;
import service.coreapi.UpdateStockCommand;
import service.database.StockEntity;
import service.database.StockEntityRepository;
import service.database.WarehouseEntity;
import service.database.WarehouseEntityRepository;

import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;


@ProcessingGroup("stockEvents")
@RestController
public class StockConsumer {

    private final transient CommandGateway commandGateway;
    private final transient CommandBus commandBus;

    private final WarehouseEntityRepository warehouseEntityRepository;
    private final StockEntityRepository stockEntityRepository;

    @Autowired
    public StockConsumer(CommandGateway commandGateway, CommandBus commandBus, StockEntityRepository stockEntityRepository, WarehouseEntityRepository warehouseEntityRepository) {
        this.commandGateway = commandGateway;
        this.commandBus = commandBus;
        this.stockEntityRepository = stockEntityRepository;
        this.warehouseEntityRepository = warehouseEntityRepository;
    }

    @EventHandler
    public void on(StockUpdateTriggeredEvent event) {

        WarehouseEntity warehouseEntity = warehouseEntityRepository.findByArticle("shirt");

        if (warehouseEntity.getAvailability() >= event.getQuantity()) {

            System.out.println("\nStock Id =                                   " + event.getStockId());
            System.out.println("Article Id =                                 " + warehouseEntity.getArticleId());
            System.out.println("Number of " + event.getArticle() +
                    "s inside the warehouse =      " + warehouseEntity.getAvailability());
            System.out.println("Number of " + event.getArticle() +
                    "s ordered =                   " + event.getQuantity());

            warehouseEntity.setAvailability(warehouseEntity.getAvailability() - event.getQuantity());

            System.out.println("New number of " + event.getArticle() +
                    "s inside the warehouse =  " + warehouseEntity.getAvailability() + "\n");

            warehouseEntityRepository.save(warehouseEntity);
            stockEntityRepository.save(new StockEntity(event.getStockId(), warehouseEntity.getArticleId(), event.getArticle(), event.getQuantity()));

            commandBus.dispatch(asCommandMessage(new TriggerStockUpdateCommand(event.getStockId(), event.getArticleId(), event.getArticle(), event.getQuantity())));
            commandGateway.send(new UpdateStockCommand(event.getStockId(), event.getArticleId(), event.getArticle(), event.getQuantity()));
        } else {
            System.out.println("\nYou don't have enough article in the warehouse!\n");
            commandBus.dispatch(asCommandMessage(new TriggerStockUpdateCommand(event.getStockId(), event.getArticleId(), event.getArticle(), event.getQuantity())));
            commandGateway.send(new AbortStockCommand(event.getStockId(), event.getArticleId(), event.getArticle(), event.getQuantity()));

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
