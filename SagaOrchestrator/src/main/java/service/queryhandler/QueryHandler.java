package service.queryhandler;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import service.coreapi.*;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class QueryHandler {

    public QueryHandler() {
    }

    @CommandHandler
    public QueryHandler(QueryHandlerSaveOrderCommand command) {
        apply(new QueryHandlerOrderSavedEvent(command.getOrderId(),
                command.getUser(), command.getArticle(), command.getQuantity(), command.getPrice()));
    }

    @CommandHandler
    public void handle(QueryHandlerSavePaymentCommand command) {
        apply(new QueryHandlerPaymentSavedEvent(command.getAccountId(),
                command.getUser(), command.getPaymentId(), command.getAmount()));
    }

    @CommandHandler
    public void handle(QueryHandlerSaveStockCommand command) {
        apply(new QueryHandlerStockSavedEvent(command.getArticleId(),
                command.getArticle(), command.getStockId(), command.getQuantity()));
    }


    @EventSourcingHandler
    public void on(QueryHandlerOrderSavedEvent event) {

    }

    @EventSourcingHandler
    public void on(QueryHandlerPaymentSavedEvent event) {

    }

    @EventSourcingHandler
    public void on(QueryHandlerStockSavedEvent event) {

    }
}
