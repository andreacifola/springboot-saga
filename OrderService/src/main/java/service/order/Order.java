package service.order;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import service.coreapi.*;
import service.database.OrderEntity;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Order {

    @AggregateIdentifier
    private String orderId;

    private OrderEntity order = new OrderEntity();

    public Order() {

    }

    @CommandHandler
    public Order(CreateOrderCommand command) {
        apply(new OrderCreatedEvent(command.getOrderId(), command.getUser(),
                command.getArticle(), command.getQuantity(), command.getPrice()));
    }

    @EventSourcingHandler
    public OrderEntity on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        order.setOrderId(orderId);
        order.setUser(event.getUser());
        order.setArticle(event.getArticle());
        order.setQuantity(event.getQuantity());
        order.setPrice(event.getPrice());

        return order;
    }

    @CommandHandler
    public void handle(DeleteOrderCommand command) {
        apply(new OrderDeletedEvent(command.getOrderId(), command.getUser(),
                command.getArticle(), command.getQuantity(), command.getPrice()));
    }

    @CommandHandler
    public void handle(TriggerEndSagaOrderCommand command) {
        apply(new EndSagaOrderTriggeredEvent(command.getOrderId(), command.getUser(),
                command.getArticle(), command.getQuantity(), command.getPrice()));
    }





    @EventSourcingHandler
    public OrderEntity on(OrderDeletedEvent event) {
        order.setOrderId(null);
        order.setUser(null);
        order.setArticle(null);
        order.setQuantity(null);
        order.setPrice(null);

        System.out.flush();
        System.out.println("\nDeleting the order...");
        printOrderElements();
        System.out.println("GOT DeleteOrderCommand");
        return order;
    }

    @EventSourcingHandler
    public void on(EndSagaOrderTriggeredEvent event) {
        System.out.println("GOT TriggerEndSagaOrderCommand");
    }

    private void printOrderElements() {
        System.out.println("\nOrder Id =  " + order.getOrderId());
        System.out.println("User =      " + order.getUser());
        System.out.println("Article =   " + order.getArticle());
        System.out.println("Quantity =  " + order.getQuantity());
        System.out.println("Price =     " + order.getPrice()+ "\n");
    }
}
