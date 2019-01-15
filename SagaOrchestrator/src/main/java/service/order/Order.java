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
    public Order(StartSagaCommand cmd) {
        apply(new SagaStartedEvent(cmd.getOrderId(), cmd.getUser(), cmd.getArticle(), cmd.getQuantity(), cmd.getPrice()));
    }

    @CommandHandler
    public void handle(DeleteOrderCommand cmd) {
        apply(new OrderDeletedEvent(cmd.getOrderId(), cmd.getUser(), cmd.getArticle(), cmd.getQuantity(), cmd.getPrice()));
    }

    @CommandHandler
    public void handle(EndSagaOrderCommand cmd) {
        apply(new SagaOrderEndedEvent(cmd.getOrderId(), cmd.getUser(), cmd.getArticle(), cmd.getQuantity(), cmd.getPrice()));
    }



    @EventSourcingHandler
    public OrderEntity on(SagaStartedEvent event) {

        this.orderId = event.getOrderId();
        order.setOrderID(orderId);
        order.setUser(event.getUser());
        order.setArticle(event.getArticle());
        order.setQuantity(event.getQuantity());
        order.setPrice(event.getPrice());

        return order;
    }

    @EventSourcingHandler
    public OrderEntity on(OrderDeletedEvent event) {
        order = new OrderEntity();
        return order;
    }

    @EventSourcingHandler
    public void on(SagaOrderEndedEvent event) {

    }

    private void printOrderElements() {
        System.out.println("\nOrder Id =  " + order.getOrderID());
        System.out.println("User =      " + order.getUser());
        System.out.println("Article =   " + order.getArticle());
        System.out.println("Quantity =  " + order.getQuantity());
        System.out.println("Price =     " + order.getPrice()+ "\n");
    }
}
