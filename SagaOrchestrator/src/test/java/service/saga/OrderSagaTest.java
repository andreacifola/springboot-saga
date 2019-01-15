package service.saga;

import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;
import service.coreapi.*;

public class OrderSagaTest {

    private SagaTestFixture<OrderSaga> fixture;

    @Before
    public void setUp() {
        fixture = new SagaTestFixture<>(OrderSaga.class);
    }

    @Test
    public void testStartSaga() {
        fixture.givenNoPriorActivity()
                .whenPublishingA(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"))
                .expectActiveSagas(1);
    }

    @Test
    public void testEndSaga() {
        fixture.givenAPublished(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"))
                .andThenAPublished(new StockUpdateEnabledEvent("3456", "0987", "Alice", "30$"))
                .whenPublishingA(new StockSagaEndedEvent("3456", "456", "shirt", 2))
                .expectNoDispatchedCommands();
    }

    @Test
    public void testEndSagaWithCompensation() {
        fixture.givenAPublished(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"))
                .andThenAPublished(new StockUpdateEnabledEvent("3456", "0987", "Alice", "30$"))
                .andThenAPublished(new CompensatePaymentTriggeredEvent("5678", "456", "shirt", 2))
                .whenPublishingA(new OrderCompensateTriggeredEvent("3456", "0987", "shirt", "30$"))
                .expectNoDispatchedCommands();
    }

}