package service.sagaamqp;

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
import service.SagaOrchestratorApplication;
import service.coreapi.*;

import java.util.UUID;


/**
 * This class receives the events from the OrderService
 */
@ProcessingGroup("sagaOrderEvents")
@RestController
public class SagaOrderConsumer {

    private final CommandGateway commandGateway;
    public static String orderID;

    public SagaOrderConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    /**
     * When SagaOrchestrator receives the OrderCreatedEvent, it starts the saga and send to the OrderService the
     * StartSagaCommand in order to allow it to store the Order; besides it sends the QueryHandlerSaveOrderCommand
     * to the QueryHandler in order to allow it to store the first wave of global information.
     *
     * @param event
     * @return
     */
    @EventHandler
    public void on(OrderCreatedEvent event) {

        orderID = event.getOrderId();

        SagaOrchestratorApplication.sagaId = UUID.randomUUID().toString();
        System.out.println("\n--------------------------------------------------- Start Saga " +
                SagaOrchestratorApplication.sagaId + " ----------------------------------------------------");
        SagaOrchestratorApplication.logger.info("Start Saga " + SagaOrchestratorApplication.sagaId + "\n");
        System.out.println("\n-------------------------------------------------- Create Order " +
                SagaOrchestratorApplication.sagaId + " ---------------------------------------------------");
        SagaOrchestratorApplication.logger.info("Create Order " + SagaOrchestratorApplication.sagaId + "\n");
        printOrderElements(event);

        commandGateway.send(new StartSagaCommand(event.getOrderId(),
                event.getUser(), event.getArticle(), event.getQuantity(), event.getPrice()));

        commandGateway.send(new QueryHandlerSaveOrderCommand(event.getOrderId(),
                event.getUser(), event.getArticle(), event.getQuantity(), event.getPrice()));

    }

    /**
     * When SagaOrchestrator receives the EndSagaOrderTriggeredEvent, it means that everything goes well so
     * it can end the saga.
     * @param event
     * @return
     */
    @EventHandler
    public void on(EndSagaOrderTriggeredEvent event) {
        commandGateway.send(new EndSagaOrderCommand(event.getOrderId(),
                event.getUser(), event.getArticle(), event.getQuantity(), event.getPrice()));
    }

    private void printOrderElements(OrderCreatedEvent event) {
        System.out.println("\nOrder Id =  " + event.getOrderId());
        System.out.println("User =      " + event.getUser());
        System.out.println("Article =   " + event.getArticle());
        System.out.println("Quantity =  " + event.getQuantity());
        System.out.println("Price =     " + event.getPrice() + "\n");
    }

    /**
     * This is the classic method used by Axon to listen messages
     * from the specified Queue, in this case the OrderSaga queue.
     * @param serializer
     * @return
     */
    @Bean
    public SpringAMQPMessageSource sagaOrderQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "OrderSaga")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }
}
