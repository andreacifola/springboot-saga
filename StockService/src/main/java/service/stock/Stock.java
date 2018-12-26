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
    private String articleId;
    private String article;
    private String stockId;
    private Integer quantity;
    private Integer available;

    public Stock() {

    }

    @CommandHandler
    public Stock(UpdateStockCommand command) {
        apply(new StockUpdatedEvent(command.getArticleId(), command.getArticle(), command.getStockId(), command.getQuantity()));
    }

    @CommandHandler
    public void handle(EnableEndSagaCommand command) {
        apply(new EndSagaEnabledEvent(command.getArticleId(), command.getArticle(), command.getStockId(), command.getQuantity()));
    }

    @CommandHandler
    public void handle(CompensatePaymentCommand command) {
        apply(new PaymentCompensatedEvent(command.getArticleId(), command.getArticle(), command.getStockId(), command.getQuantity()));
    }


    @EventSourcingHandler
    public void on(StockUpdatedEvent event) {
        this.articleId = event.getArticleId();
        this.stockId = event.getStockId();
        this.article = event.getArticle();
        this.quantity = event.getQuantity();
    }

    @EventSourcingHandler
    public void on(EndSagaEnabledEvent event) {

    }

    @EventSourcingHandler
    public void on(PaymentCompensatedEvent event) {

    }
}
