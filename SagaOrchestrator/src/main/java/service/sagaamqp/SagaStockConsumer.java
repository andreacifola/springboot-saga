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
import service.coreapi.EndSagaEnabledEvent;
import service.coreapi.EndSagaStockCommand;
import service.coreapi.PaymentCompensatedEvent;

@ProcessingGroup("sagaStockEvents")
@RestController
public class SagaStockConsumer {

    private final transient CommandGateway commandGateway;

    public SagaStockConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(EndSagaEnabledEvent event) {
        commandGateway.send(new EndSagaStockCommand(event.getArticleId(),
                event.getArticle(), event.getStockId(), event.getQuantity()));
    }

    @EventHandler
    public void on(PaymentCompensatedEvent event) {
        //todo send right command
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
