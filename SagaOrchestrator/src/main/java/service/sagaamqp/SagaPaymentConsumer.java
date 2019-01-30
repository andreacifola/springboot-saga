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


@ProcessingGroup("sagaPaymentEvents")
@RestController
public class SagaPaymentConsumer {

    private final CommandGateway commandGateway;

    public SagaPaymentConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    /**
     * When SagaOrchestrator receives the PaymentDoneEvent, it means that the PaymentService has done
     * positively its transaction, so it can enable the stock update sending the EnableStockUpdateCommand.
     *
     * @param event
     * @return
     */
    @EventHandler
    public void on(PaymentDoneEvent event) {
        commandGateway.send(new EnableStockUpdateCommand(event.getPaymentId(), event.getAccountId(), event.getUser(), event.getAmount()));
    }

    /**
     * When SagaOrchestrator receives the PaymentAbortedEvent, it means that something went wrong in the PaymentService,
     * so it has to start the compensating action in the OrderService via the TriggerCompensateOrderCommand.
     * @param event
     * @return
     */
    @EventHandler
    public void on(PaymentAbortedEvent event) {
        commandGateway.send(new TriggerCompensateOrderCommand(event.getPaymentId(), event.getAccountId(), event.getUser(), event.getAmount()));
    }

    /**
     * When SagaOrchestrator receives the EndSagaPaymentTriggeredEvent, it means that something went wrong in the
     * PaymentService and it has sent the end saga command; so it sends the EndSagaPaymentCommand .
     * @param event
     * @return
     */
    @EventHandler
    public void on(EndSagaPaymentTriggeredEvent event) {
        commandGateway.send(new EndSagaPaymentCommand(event.getPaymentId(), event.getAccountId(), event.getUser(), event.getAmount()));
    }

    /**
     * This is the classic method used by Axon to listen messages
     * from the specified Queue, in this case the PaymentSaga queue.
     * @param serializer
     * @return
     */
    @Bean
    public SpringAMQPMessageSource sagaPaymentQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "PaymentSaga")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }
}
