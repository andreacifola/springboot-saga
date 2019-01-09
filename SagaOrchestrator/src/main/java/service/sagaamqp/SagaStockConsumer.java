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
import service.coreapi.EndSagaStockCommand;
import service.coreapi.StockAbortedEvent;
import service.coreapi.StockUpdatedEvent;
import service.coreapi.TriggerCompensatePaymentCommand;

@ProcessingGroup("sagaStockEvents")
@RestController
public class SagaStockConsumer {

    private final transient CommandGateway commandGateway;

    public SagaStockConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(StockUpdatedEvent event) {
        commandGateway.send(new EndSagaStockCommand(event.getStockId(),
                event.getArticle(), event.getQuantity()));
    }

    @EventHandler
    public void on(StockAbortedEvent event) {
        commandGateway.send(new TriggerCompensatePaymentCommand(event.getStockId(),
                event.getArticle(), event.getQuantity()));
    }

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
