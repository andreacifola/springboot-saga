package servicesagaorchestrator.stock;


import servicesagaorchestrator.SagaOrchestratorApplication;
import servicesagaorchestrator.coreapi.StockUpdatedEvent;
import servicesagaorchestrator.coreapi.UpdateStockCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import servicesagaorchestrator.entities.WareHouseEntity;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Stock {

    @AggregateIdentifier
    private String article;
    private Integer quantity;
    private Integer available;

    private WareHouseEntity wareHouseEntity = new WareHouseEntity("shirt", 23);

    public Stock() {
    }

    @CommandHandler
    public Stock(UpdateStockCommand command) throws NotEnoughArticlesInTheStockException {
        available = wareHouseEntity.getAvailable();
        if (available >= command.getQuantity()) {
            apply(new StockUpdatedEvent(SagaOrchestratorApplication.stockId, command.getArticle(), command.getStockId(), command.getQuantity()));
        } else {
            System.out.println("You don't have enough " + command.getArticle() + "s in the warehouse!\n");
            throw new NotEnoughArticlesInTheStockException();
        }
    }



    @EventSourcingHandler
    public Integer on(StockUpdatedEvent event) {
        this.article = event.getArticle();
        this.quantity = event.getQuantity();

        System.out.println("\nNumber of " + event.getArticle() + "s inside the warehouse = " + wareHouseEntity.getAvailable());
        System.out.println("Number of "+ event.getArticle() + "s ordered = " + event.getQuantity());

        wareHouseEntity.setAvailable(wareHouseEntity.getAvailable() - event.getQuantity());

        System.out.println("New number of " + event.getArticle() + "s inside the warehouse = " + wareHouseEntity.getAvailable() + "\n");
        return wareHouseEntity.getAvailable();
    }
}
