package service.orderamqp;


import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import service.coreapi.OrderDeletedEvent;
import service.coreapi.SagaStartedEvent;
import service.coreapi.TriggerEndSagaOrderCommand;
import service.database.OrderEntity;
import service.database.OrderEntityRepository;


/**
 * This class receives the events from the SagaOrchestrator and listens to all the messages useful for itself
 */
@ProcessingGroup("orderEvents")
@RestController
public class OrderConsumer {

    private OrderEntity order = new OrderEntity();
    private final CommandGateway commandGateway;

    @Autowired
    private OrderEntityRepository repository;

    public OrderConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    /**
     * When OrderService receives the SagaStartedEvent, it saves in the db the order done and prints the elements.
     *
     * @param event
     * @return
     */
    @EventHandler
    public OrderEntity on(SagaStartedEvent event) {

        order.setOrderId(event.getOrderId());
        order.setUser(event.getUser());
        order.setArticle(event.getArticle());
        order.setQuantity(event.getQuantity());
        order.setPrice(event.getPrice());

        repository.save(order);
        printOrderElements();

        return order;
    }

    /**
     * When OrderService receives the OrderDeletedEvent, it deletes the specified order
     * from the db and sends the command to end the saga to the SagaOrchestrator.
     * @param event
     * @return
     */
    @EventHandler
    public OrderEntity on(OrderDeletedEvent event) {

        OrderEntity oldOrder = repository.findByOrderId(event.getOrderId());
        repository.delete(oldOrder);

        order = new OrderEntity();

        System.out.flush();
        System.out.println("\nDeleting the order...");

        printOrderElements();

        commandGateway.send(new TriggerEndSagaOrderCommand(event.getOrderId(),
                event.getUser(), event.getArticle(), event.getQuantity(), event.getPrice()));
        return order;
    }

    private void printOrderElements() {
        System.out.println("\nOrder Id =  " + order.getOrderId());
        System.out.println("User =      " + order.getUser());
        System.out.println("Article =   " + order.getArticle());
        System.out.println("Quantity =  " + order.getQuantity());
        System.out.println("Price =     " + order.getPrice()+ "\n");
    }

    /**
     * This is the classic method used by Axon to listen messages
     * from the specified Queue, in this case the Order queue.
     * @param serializer
     * @return
     */
    @Bean
    public SpringAMQPMessageSource orderQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "Order")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }
}
