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
import service.coreapi.OrderDeletedEvent;
import service.coreapi.QueryHandlerOrderSavedEvent;
import service.coreapi.QueryHandlerStockSavedEvent;
import service.database.GlobalInformation;
import service.database.GlobalInformationRepository;

//import service.coreapi.StockUpdateTriggeredEvent;

@ProcessingGroup("queryHandlerEvents")
@RestController
public class QueryHandlerConsumer {

    //TODO ridare i nomi corretti ai metodi degli eventi (solo con questi nomi riceve messaggi dall'orchestratore)

    private final GlobalInformationRepository repository;

    private String orderId;
    private String user;
    private String article;
    private String price;

    @Autowired
    public QueryHandlerConsumer(GlobalInformationRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(QueryHandlerOrderSavedEvent event) {
        //TODO rimuovere alla fine
        repository.deleteAll();

        this.orderId = event.getOrderId();
        this.user = event.getUser();
        this.article = event.getArticle();
        this.price = event.getPrice();
        GlobalInformation info = new GlobalInformation(event.getOrderId(), event.getUser(), event.getArticle(), event.getQuantity(), null, event.getPrice());
        repository.save(info);

        System.out.println(info);
    }

    @EventHandler
    public void on(QueryHandlerStockSavedEvent event) {
        GlobalInformation info = repository.findByOrderId(orderId);
        //TODO passare l'availability dallo stockService
        info.setAvailability(event.getAvailability());
        repository.save(info);

        System.out.println(info);

        System.out.println("<< Hi " + user + ",\n   You have spent " + price + " to buy " +
                event.getQuantity() + " " + article + "s out of " + event.getAvailability() +
                " available in the warehouse.\n   So now we" + " have " +
                (event.getAvailability() - event.getQuantity()) + " " + article + "s in the warehouse. >>\n");
    }

    @EventHandler
    public void on(OrderDeletedEvent event) {
        GlobalInformation info = repository.findByOrderId(orderId);
        if (info != null) {
            repository.delete(info);
            System.out.println("\nSomwthing went wrong during the saga.\nStarted the compensating actions." +
                    "\nGlobal information deleted.\n");
        } else {
            System.out.println("There are no orders to delete!");
        }
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
