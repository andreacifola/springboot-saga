package service.saga;

import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;
import service.coreapi.*;


public class OrderSagaTest {


    private SagaTestFixture<OrderSaga> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new SagaTestFixture<>(OrderSaga.class);
    }

    @Test
    public void testPaymentRequest() throws Exception {
        fixture.givenNoPriorActivity()
                .whenPublishingA(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"))
                .expectActiveSagas(1)
                .expectDispatchedCommands(new TriggerPaymentCommand("1sd3gg54", "5555", "Alice", "30$"));
    }

    @Test
    public void testStockUpdatingAfterPayment() throws Exception {
        fixture.givenAPublished(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"))
                .whenPublishingA(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .expectDispatchedCommands(new TriggerStockUpdateCommand("45g4ds3", "shirt", "9876", 2));
    }

    @Test
    public void testEndSagaAfterStockUpdated() throws Exception {
        fixture.givenAPublished(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"))
                .andThenAPublished(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .whenPublishingA(new StockUpdateTriggeredEvent("45g4ds3", "shirt", "9876", 2))
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }
}