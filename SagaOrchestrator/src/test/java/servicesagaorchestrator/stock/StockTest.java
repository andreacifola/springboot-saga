package servicesagaorchestrator.stock;

import com.example.demo.coreapi.StockUpdatedEvent;
import com.example.demo.coreapi.UpdateStockCommand;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import servicesagaorchestrator.entities.WareHouseEntity;

import static org.junit.Assert.*;


public class StockTest {

    private FixtureConfiguration<Stock> fixture;
    private Stock stock = new Stock();

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Stock.class);
    }

    @Test
    public void testUpdateStock() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new UpdateStockCommand("shirt", "9876", 2))
                .expectEvents(new StockUpdatedEvent("shirt", "9876", 2));
    }

    @Test
    public void testStockUpdated() throws Exception {
        WareHouseEntity wareHouseEntity = new WareHouseEntity("shirt", 23);
        Integer available = stock.on(new StockUpdatedEvent("shirt", "9876", 2));
        assertEquals("The available articles are the same", available.longValue(), 21L);
    }

    @Test
    public void testWrongUpdateStock() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new UpdateStockCommand("shirt", "9876", 27))
                .expectNoEvents()
                .expectException(NotEnoughArticlesInTheStockException.class);
    }

    /*@Test
    public void testStockUpdateTwice() throws Exception {
        fixture.given(new StockUpdatedEvent("shirt", 22))
                .when(new UpdateStockCommand("shirt", 5))
                .expectNoEvents()
                .expectException(NotEnoughArticlesInTheStockException.class);
    }*/
}