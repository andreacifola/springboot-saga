package servicesagaorchestrator.saga;

import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;
import servicesagaorchestrator.coreapi.*;


public class OrderSagaTest {


    private SagaTestFixture<OrderSaga> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new SagaTestFixture<>(OrderSaga.class);
    }

    @Test
    public void testPaymentRequest() throws Exception {
        fixture.givenNoPriorActivity()
                .whenPublishingA(new OrderCreatedEvent("1234", "Alice", "shirt", 2, "30$"))
                .expectActiveSagas(1)
                .expectDispatchedCommands(new DoPaymentCommand("Alice", "5555", "30$"));
    }

    @Test
    public void testStockUpdatingAfterPayment() throws Exception {
        fixture.givenAPublished(new OrderCreatedEvent("1234", "Alice", "shirt", 2, "30$"))
                .whenPublishingA(new PaymentDoneEvent("Alice", "5555", "30$"))
                .expectDispatchedCommands(new UpdateStockCommand("45g4ds3", "shirt", "9876", 2));
    }

    @Test
    public void testEndSagaAfterStockUpdated() throws Exception {
        fixture.givenAPublished(new OrderCreatedEvent("1234", "Alice", "shirt", 2, "30$"))
                .andThenAPublished(new PaymentDoneEvent("Alice", "5555", "30$"))
                .whenPublishingA(new StockUpdatedEvent("45g4ds3", "shirt", "9876", 2))
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }
}