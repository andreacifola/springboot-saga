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

    @EventHandler
    public void on(PaymentDoneEvent event) {
        commandGateway.send(new EnableStockUpdateCommand(event.getPaymentId(), event.getUser(), event.getAmount()));
    }

    @EventHandler
    public void on(PaymentAbortedEvent event) {

        commandGateway.send(new TriggerCompensateOrderCommand(event.getPaymentId(), event.getUser(), event.getAmount()));
    }

    @EventHandler
    public void on(EndSagaPaymentTriggeredEvent event) {

        commandGateway.send(new EndSagaPaymentCommand(event.getPaymentId(), event.getUser(), event.getAmount()));
    }

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
