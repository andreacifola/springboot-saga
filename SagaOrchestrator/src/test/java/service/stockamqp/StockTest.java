package service.stockamqp;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;
import service.coreapi.StockUpdateTriggeredEvent;
import service.coreapi.TriggerStockUpdateCommand;
import service.stock.NotEnoughArticlesInTheStockException;
import service.stock.Stock;


public class StockTest {

    private Stock stock = new Stock();
    private AggregateTestFixture<Stock> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AggregateTestFixture<>(Stock.class); //version 3.4.1
    }

    @Test
    public void testUpdateStock() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new TriggerStockUpdateCommand("45g4ds3", "shirt", "9876", 2))
                .expectEvents(new StockUpdateTriggeredEvent("45g4ds3", "shirt", "9876", 2));
    }

    /*
    @Test
    public void testStockUpdated() throws Exception {
        WareHouseEntity wareHouseEntity = new WareHouseEntity("shirt", 23);
        Integer available = stock.on(new StockUpdateTriggeredEvent("45g4ds3", "shirt", "9876", 2));
        assertEquals("The available articles are the same", available.longValue(), 21L);
    }
    */

    @Test
    public void testWrongUpdateStock() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new TriggerStockUpdateCommand("45g4ds3", "shirt", "9876", 27))
                .expectNoEvents()
                .expectException(NotEnoughArticlesInTheStockException.class);
    }

    /*@Test
    public void testStockUpdateTwice() throws Exception {
        fixture.given(new StockUpdateTriggeredEvent("shirt", 22))
                .when(new TriggerStockUpdateCommand("shirt", 5))
                .expectNoEvents()
                .expectException(NotEnoughArticlesInTheStockException.class);
    }*/
}