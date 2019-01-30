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
import service.coreapi.*;

@ProcessingGroup("sagaStockEvents")
@RestController
public class SagaStockConsumer {

    private final transient CommandGateway commandGateway;

    public SagaStockConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    /**
     * When SagaOrchestrator receives the StockUpdatedEvent, it means that the StockService has done
     * positively its transaction, so it can end the saga via the EndSagaStockCommand;
     * besides it sends the QueryHandlerSaveStockCommand to the QueryHandler in order
     * to allow it to store the second wave of global information.
     *
     * @param event
     * @return
     */
    @EventHandler
    public void on(StockUpdatedEvent event) {
        commandGateway.send(new EndSagaStockCommand(event.getStockId(),
                event.getArticleId(), event.getArticle(), event.getQuantity()));

        commandGateway.send(new QueryHandlerSaveStockCommand(event.getStockId(),
                event.getArticleId(), event.getArticle(), event.getQuantity(), event.getAvailability()));
    }

    /**
     * When SagaOrchestrator receives the StockAbortedEvent, it means that something went wrong in the StockService,
     * so it has to start the compensating action in the PaymentService via the TriggerCompensatePaymentCommand.
     * @param event
     * @return
     */
    @EventHandler
    public void on(StockAbortedEvent event) {
        commandGateway.send(new TriggerCompensatePaymentCommand(event.getStockId(),
                event.getArticleId(), event.getArticle(), event.getQuantity()));
    }

    /**
     * This is the classic method used by Axon to listen messages
     * from the specified Queue, in this case the StockSaga queue.
     * @param serializer
     * @return
     */
    @Bean
    public SpringAMQPMessageSource sagaStockQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "StockSaga")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }
}
