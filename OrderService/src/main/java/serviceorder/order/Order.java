package serviceorder.order;


import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import serviceorder.coreapi.OrderCreatedEvent;
import serviceorder.coreapi.OrderDeletedEvent;
import serviceorder.entities.OrderEntity;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;


@ProcessingGroup("orderEvents")
@RestController
public class Order {

    private OrderEntity order = new OrderEntity();

    @EventHandler
    public OrderEntity on(OrderCreatedEvent event) {
        order.setOrderID(event.getOrderId());
        order.setUser(event.getUser());
        order.setArticle(event.getArticle());
        order.setQuantity(event.getQuantity());
        order.setPrice(event.getPrice());

        printOrderElements();

        return order;
    }

    @EventHandler
    public OrderEntity on(OrderDeletedEvent event) {
        order = new OrderEntity();

        System.out.flush();
        System.out.println("\nDeleting the order...");
        printOrderElements();

        return order;
    }

    private void printOrderElements() {
        System.out.println("\nOrder Id =  " + order.getOrderID());
        System.out.println("User =      " + order.getUser());
        System.out.println("Article =   " + order.getArticle());
        System.out.println("Quantity =  " + order.getQuantity());
        System.out.println("Price =     " + order.getPrice()+ "\n");
    }

    @Bean
    public SpringAMQPMessageSource orderQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "Order")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
                System.out.println("ack");
            }
        };
    }
}
