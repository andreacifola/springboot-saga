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
import service.coreapi.StartSagaCommand;
import service.coreapi.OrderCreatedEvent;


@ProcessingGroup("sagaEvents")
@RestController
public class SagaConsumer {

    private final CommandGateway commandGateway;

    public SagaConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        commandGateway.send(new StartSagaCommand(event.getOrderId(),
                event.getUser(), event.getArticle(), event.getQuantity(), event.getPrice()));
    }

    @Bean
    public SpringAMQPMessageSource sagaQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "OrderSaga")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }
}
