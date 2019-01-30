package service.queryhandler;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import service.coreapi.*;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class QueryHandler {

    @AggregateIdentifier
    private String orderId;

    public QueryHandler() {
    }

    //This methods annotated with @CommandHandler are used to launch an event.

    @CommandHandler
    public QueryHandler(QueryHandlerSaveOrderCommand command) {
        apply(new QueryHandlerOrderSavedEvent(command.getOrderId(),
                command.getUser(), command.getArticle(), command.getQuantity(), command.getPrice()));
    }

    @CommandHandler
    public void handle(QueryHandlerSaveStockCommand command) {
        apply(new QueryHandlerStockSavedEvent(command.getArticleId(), command.getArticle(),
                command.getStockId(), command.getQuantity(), command.getAvailability()));
    }

    @CommandHandler
    public void handle(DeleteOrderCommand command) {
        apply(new OrderDeletedEvent(command.getOrderId(), command.getUser(),
                command.getArticle(), command.getQuantity(), command.getPrice()));
    }




    @EventSourcingHandler
    public void on(QueryHandlerOrderSavedEvent event) {
        this.orderId = event.getOrderId();

    }

    @EventSourcingHandler
    public void on(QueryHandlerStockSavedEvent event) {

    }

    @EventSourcingHandler
    public void on(OrderDeletedEvent event) {

    }
}
