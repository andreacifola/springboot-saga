package service.queryhandleramqp;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import service.coreapi.QueryHandlerOrderSavedEvent;
import service.coreapi.QueryHandlerStockSavedEvent;
import service.database.GlobalInformation;
import service.database.GlobalInformationRepository;

//import service.coreapi.StockUpdateTriggeredEvent;

@ProcessingGroup("queryHandlerEvents")
@RestController
public class QueryHandlerConsumer {

    //TODO ridare i nomi corretti ai metodi degli eventi (solo con questi nomi riceve messaggi dall'orchestratore)

    @Autowired
    private GlobalInformationRepository repository;

    private String orderId;

    @EventHandler
    public void on(QueryHandlerOrderSavedEvent event) {
        //TODO rimuovere alla fine
        repository.deleteAll();

        this.orderId = event.getOrderId();
        GlobalInformation info = new GlobalInformation(event.getOrderId(), event.getUser(), event.getArticle(), event.getQuantity(), null, event.getPrice());
        repository.save(info);

        System.out.println(info);
    }

    @EventHandler
    public void on(QueryHandlerStockSavedEvent event) {
        GlobalInformation info = repository.findByOrderId(orderId);
        //TODO passare l'availability dallo stockService
        info.setAvailability(event.getQuantity());
        repository.save(info);

        System.out.println(info);
    }

    @Bean
    public SpringAMQPMessageSource queryHandlerQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "QueryHandler")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }
}
