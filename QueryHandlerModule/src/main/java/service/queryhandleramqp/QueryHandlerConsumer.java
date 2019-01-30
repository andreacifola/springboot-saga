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


/**
 * This class receives the events from the SagaOrchestrator and listens to all the messages useful for itself
 */
@ProcessingGroup("queryHandlerEvents")
@RestController
public class QueryHandlerConsumer {

    private final GlobalInformationRepository repository;

    private String orderId;
    private String user;
    private String article;
    private String price;

    @Autowired
    public QueryHandlerConsumer(GlobalInformationRepository repository) {
        this.repository = repository;
    }

    /**
     * When QueryHandler receives the QueryHandlerOrderSavedEvent, it saves in the db
     * the first wave of information that comes from OrderService.
     *
     * @param event
     * @return
     */
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

    /**
     * When QueryHandler receives the QueryHandlerOrderSavedEvent, it saves in the db
     * the second wave of information that comes from StockService.
     * @param event
     * @return
     */
    @EventHandler
    public void on(QueryHandlerStockSavedEvent event) {
        GlobalInformation info = repository.findByOrderId(orderId);
        info.setAvailability(event.getAvailability());
        repository.save(info);

        System.out.println(info);

        System.out.println("<< Hi " + user + ",\n   You have spent " + price + " to buy " +
                event.getQuantity() + " " + article + "s out of " + event.getAvailability() +
                " available in the warehouse.\n   So now we" + " have " +
                (event.getAvailability() - event.getQuantity()) + " " + article + "s in the warehouse. >>\n");
    }

    /**
     * When QueryHandler receives the QueryHandlerOrderSavedEvent, it means that the saga was interrupted in
     * some point. So we need to delete the global information in the db.
     * @param event
     * @return
     */
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

    /**
     * This is the classic method used by Axon to listen messages
     * from the specified Queue, in this case the QueryHandler queue.
     * @param serializer
     * @return
     */
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
