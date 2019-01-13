package service.stock;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import service.coreapi.*;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Stock {

    @AggregateIdentifier
    private String stockId;
    private String articleId;
    private String article;
    private Integer quantity;
    private Integer available;

    public Stock() {

    }

    @CommandHandler
    public Stock(TriggerStockUpdateCommand command) {
        apply(new StockUpdateTriggeredEvent(command.getStockId(), command.getArticleId(), command.getArticle(), command.getQuantity()));
    }

    @CommandHandler
    public void handle(StockUpdateCommand command) {
        apply(new StockUpdatedEvent(command.getStockId(), command.getArticleId(), command.getArticle(), command.getQuantity(), command.getAvailability()));
    }

    @CommandHandler
    public void handle(AbortStockCommand command) {
        apply(new StockAbortedEvent(command.getStockId(), command.getArticleId(), command.getArticle(), command.getQuantity()));
    }

    @CommandHandler
    public void handle(EndSagaStockCommand command) {
        apply(new StockSagaEndedEvent(command.getStockId(), command.getArticleId(), command.getArticle(), command.getQuantity()));
    }

    @CommandHandler
    public void handle(TriggerCompensatePaymentCommand command) {
        apply(new CompensatePaymentTriggeredEvent(command.getStockId(), command.getArticleId(), command.getArticle(), command.getQuantity()));
    }


    @CommandHandler
    public void handle(QueryHandlerSaveStockCommand command) {
        apply(new QueryHandlerStockSavedEvent(command.getArticleId(), command.getArticle(),
                command.getStockId(), command.getQuantity(), command.getAvailability()));
    }

    @EventSourcingHandler
    public void on(QueryHandlerStockSavedEvent event) {

    }



    @EventSourcingHandler
    public void on(StockUpdateTriggeredEvent event) {
        this.stockId = event.getStockId();
        this.articleId = event.getArticleId();
        this.article = event.getArticle();
        this.quantity = event.getQuantity();
    }

    @EventSourcingHandler
    public void on(StockUpdatedEvent event) {

    }

    @EventSourcingHandler
    public void on(StockAbortedEvent event) {

    }

    @EventSourcingHandler
    public void on(StockSagaEndedEvent event) {

    }

    @EventSourcingHandler
    public void on(CompensatePaymentTriggeredEvent event) {

    }
}
