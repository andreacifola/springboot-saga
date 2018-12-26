package service.order;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import service.coreapi.*;
import service.entities.OrderEntity;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Order {

    @AggregateIdentifier
    private String orderId;

    private OrderEntity order = new OrderEntity();

    public Order() {

    }

    @CommandHandler
    public Order(StartSagaCommand command) {
        apply(new SagaStartedEvent(command.getOrderId(), command.getUser(),
                command.getArticle(), command.getQuantity(), command.getPrice()));
    }

    @CommandHandler
    public void handle(DeleteOrderCommand command) {
        apply(new OrderDeletedEvent(command.getOrderId(), command.getUser(),
                command.getArticle(), command.getQuantity(), command.getPrice()));
    }

    @CommandHandler
    public void handle(EndSagaOrderCommand command) {
        apply(new SagaEndedOrderEvent(command.getOrderId(), command.getUser(),
                command.getArticle(), command.getQuantity(), command.getPrice()));
    }



    @EventSourcingHandler
    public OrderEntity on(SagaStartedEvent event) {
        this.orderId = event.getOrderId();
        order.setOrderID(orderId);
        order.setUser(event.getUser());
        order.setArticle(event.getArticle());
        order.setQuantity(event.getQuantity());
        order.setPrice(event.getPrice());

        printOrderElements();

        return order;
    }

    @EventSourcingHandler
    public OrderEntity on(OrderDeletedEvent event) {
        order = new OrderEntity();

        System.out.flush();
        System.out.println("\nDeleting the order...");
        printOrderElements();

        return order;
    }

    @EventSourcingHandler
    public void on(SagaEndedOrderEvent event) {

    }

    private void printOrderElements() {
        System.out.println("\nOrder Id =  " + order.getOrderID());
        System.out.println("User =      " + order.getUser());
        System.out.println("Article =   " + order.getArticle());
        System.out.println("Quantity =  " + order.getQuantity());
        System.out.println("Price =     " + order.getPrice()+ "\n");
    }
}