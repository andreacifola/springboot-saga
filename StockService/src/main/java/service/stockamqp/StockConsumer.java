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
import service.coreapi.StockUpdateCommand;
import service.coreapi.StockUpdateTriggeredEvent;
import service.coreapi.TriggerStockUpdateCommand;
import service.database.StockEntity;
import service.database.StockEntityRepository;
import service.database.WarehouseEntity;
import service.database.WarehouseEntityRepository;

import java.util.UUID;

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

    /**
     * When StockService receives the StockUpdateTriggeredEvent, first of all it search the right article in the
     * warehouse db. Then it checks if the warehouse has enough articles for the order: if yes, it decreases
     * the number of articles for the wanted article, prints the transaction, save the stock in the db and
     * sends the StockUpdateCommand to the SagaOrchestrator to continue with the saga. Otherwise  it does not do
     * the transaction and sends to the SagaOrchestrator the AbortStockCommand to start the end of the saga.
     *
     * @param event
     * @return
     */
    @EventHandler
    public void on(StockUpdateTriggeredEvent event) {

        if (warehouseEntityRepository.findByArticle("shirt") == null)
            warehouseEntityRepository.save(new WarehouseEntity(UUID.randomUUID().toString(), "shirt", 230));

        WarehouseEntity warehouseEntity = warehouseEntityRepository.findByArticle("shirt");

        if (warehouseEntity.getAvailability() >= event.getQuantity()) {

            System.out.println("\nStock Id =                                   " + event.getStockId());
            System.out.println("Article Id =                                 " + warehouseEntity.getArticleId());
            System.out.println("Number of " + event.getArticle() +
                    "s inside the warehouse =      " + warehouseEntity.getAvailability());
            System.out.println("Number of " + event.getArticle() +
                    "s ordered =                   " + event.getQuantity());

            Integer oldAvailability = warehouseEntity.getAvailability();

            warehouseEntity.setAvailability(warehouseEntity.getAvailability() - event.getQuantity());

            System.out.println("New number of " + event.getArticle() +
                    "s inside the warehouse =  " + warehouseEntity.getAvailability() + "\n");

            warehouseEntityRepository.save(warehouseEntity);
            stockEntityRepository.save(new StockEntity(event.getStockId(), warehouseEntity.getArticleId(), event.getArticle(), event.getQuantity()));

            commandBus.dispatch(asCommandMessage(new TriggerStockUpdateCommand(event.getStockId(), event.getArticleId(), event.getArticle(), event.getQuantity())));
            commandGateway.send(new StockUpdateCommand(event.getStockId(), event.getArticleId(), event.getArticle(), event.getQuantity(), oldAvailability));
        } else {
            System.out.println("\nYou don't have enough article in the warehouse!\n");
            commandBus.dispatch(asCommandMessage(new TriggerStockUpdateCommand(event.getStockId(), event.getArticleId(), event.getArticle(), event.getQuantity())));
            commandGateway.send(new AbortStockCommand(event.getStockId(), event.getArticleId(), event.getArticle(), event.getQuantity()));

        }
    }

    /**
     * This is the classic method used by Axon to listen messages
     * from the specified Queue, in this case the Stock queue.
     * @param serializer
     * @return
     */
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
