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
    public QueryHandler(StartSagaCommand command) {
        apply(new SagaStartedEvent(command.getOrderId(),
                command.getUser(), command.getArticle(), command.getQuantity(), command.getPrice()));
    }

    @CommandHandler
    public void handle(TriggerPaymentCommand command) {
        apply(new PaymentTriggeredEvent(command.getAccountId(),
                command.getUser(), command.getPaymentId(), command.getAmount()));
    }

    @CommandHandler
    public void handle(TriggerStockUpdateCommand command) {
        apply(new StockUpdateTriggeredEvent(command.getArticleId(),
                command.getArticle(), command.getStockId(), command.getQuantity()));
    }


    @EventSourcingHandler
    public void on(SagaStartedEvent event) {

    }

    @EventSourcingHandler
    public void on(PaymentTriggeredEvent event) {

    }

    @EventSourcingHandler
    public void on(StockUpdateTriggeredEvent event) {

    }
}
