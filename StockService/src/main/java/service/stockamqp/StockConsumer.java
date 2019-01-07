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

    @Autowired
    private WarehouseEntityRepository warehouseEntityRepository;
    @Autowired
    private StockEntityRepository stockEntityRepository;

    public StockConsumer(CommandGateway commandGateway, CommandBus commandBus) {
        this.commandGateway = commandGateway;
        this.commandBus = commandBus;
    }

    @EventHandler
    public void on(StockUpdateTriggeredEvent event) {

        //TODO eliminare quando è finito
        stockEntityRepository.deleteAll();

        WarehouseEntity warehouseEntity = warehouseEntityRepository.findByArticle("shirt");

        if (warehouseEntity.getAvailability() >= event.getQuantity()) {

            System.out.println("\nArticle Id =                                 " + event.getArticleId());
            System.out.println("Number of " + event.getArticle() +
                    "s inside the warehouse =      " + warehouseEntity.getAvailability());
            System.out.println("Number of " + event.getArticle() +
                    "s ordered =                   " + event.getQuantity());

            warehouseEntity.setAvailability(warehouseEntity.getAvailability() - event.getQuantity());

            System.out.println("New number of " + event.getArticle() +
                    "s inside the warehouse =  " + warehouseEntity.getAvailability() + "\n");

            warehouseEntityRepository.save(warehouseEntity);
            stockEntityRepository.save(new StockEntity(event.getArticleId(), event.getArticle(), event.getQuantity()));

            commandBus.dispatch(asCommandMessage(new TriggerStockUpdateCommand(event.getArticleId(), event.getArticle(), event.getStockId(), event.getQuantity())));
            commandGateway.send(new UpdateStockCommand(event.getArticleId(), event.getArticle(), event.getStockId(), event.getQuantity()));
        } else {
            System.out.println("\nYou don't have enough article in the warehouse!\n");
            commandBus.dispatch(asCommandMessage(new TriggerStockUpdateCommand(event.getArticleId(), event.getArticle(), event.getStockId(), event.getQuantity())));
            commandGateway.send(new AbortStockCommand(event.getArticleId(), event.getArticle(), event.getStockId(), event.getQuantity()));

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
