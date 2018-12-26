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
import service.coreapi.EnablePaymentCommand;
import service.coreapi.OrderCompensatedEvent;
import service.coreapi.RefundPaymentCommand;
import service.coreapi.StockUpdateEnabledEvent;


@ProcessingGroup("sagaPaymentEvents")
@RestController
public class SagaPaymentConsumer {

    private final CommandGateway commandGateway;

    public SagaPaymentConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(StockUpdateEnabledEvent event) {
        commandGateway.send(new EnablePaymentCommand(event.getAccountId(),
                event.getUser(), event.getPaymentId(), event.getAmount()));
    }

    @EventHandler
    public void on(OrderCompensatedEvent event) {
        commandGateway.send(new RefundPaymentCommand(event.getAccountId(),
                event.getUser(), event.getPaymentId(), event.getAmount()));
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
