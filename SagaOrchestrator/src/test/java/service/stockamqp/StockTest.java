package service.stockamqp;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;
import service.coreapi.*;
import service.stock.Stock;


public class StockTest {

    private AggregateTestFixture<Stock> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AggregateTestFixture<>(Stock.class); //version 3.4.1
    }

    @Test
    public void testTriggerStockUpdate() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new TriggerStockUpdateCommand("45g4ds3", "9876", "shirt", 2))
                .expectEvents(new StockUpdateTriggeredEvent("45g4ds3", "9876", "shirt", 2));
    }

    @Test
    public void testStockUpdate() throws Exception {
        fixture.given(new StockUpdateTriggeredEvent("45g4ds3", "9876", "shirt", 2))
                .when(new StockUpdateCommand("45g4ds3", "9876", "shirt", 2, 23))
                .expectEvents(new StockUpdatedEvent("45g4ds3", "9876", "shirt", 2, 23));
    }

    @Test
    public void testAbortStock() throws Exception {
        fixture.given(new StockUpdateTriggeredEvent("45g4ds3", "9876", "shirt", 2))
                .when(new AbortStockCommand("45g4ds3", "9876", "shirt", 2))
                .expectEvents(new StockAbortedEvent("45g4ds3", "9876", "shirt", 2));
    }

    @Test
    public void testEndSagaStock() throws Exception {
        fixture.given(new StockUpdateTriggeredEvent("45g4ds3", "9876", "shirt", 2))
                .when(new EndSagaStockCommand("45g4ds3", "9876", "shirt", 2))
                .expectEvents(new StockSagaEndedEvent("45g4ds3", "9876", "shirt", 2));
    }

    @Test
    public void testTriggerCompensatePayment() throws Exception {
        fixture.given(new StockUpdateTriggeredEvent("45g4ds3", "9876", "shirt", 2))
                .when(new TriggerCompensatePaymentCommand("45g4ds3", "9876", "shirt", 2))
                .expectEvents(new CompensatePaymentTriggeredEvent("45g4ds3", "9876", "shirt", 2));
    }
}