package servicesagaorchestrator.saga;

import com.example.demo.coreapi.*;
import org.axonframework.test.saga.AnnotatedSagaTestFixture;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class OrderSagaTest {

    private AnnotatedSagaTestFixture fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AnnotatedSagaTestFixture<>(OrderSaga.class);
    }

    @Test
    public void testPaymentRequest() throws Exception {
        fixture.givenNoPriorActivity()
                .whenPublishingA(new OrderCreatedEvent("1234", "Alice", "shirt", 2, "30$"))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(new DoPaymentCommand("Alice", "5555", "30$"));
    }

    @Test
    public void testStockUpdatingAfterPayment() throws Exception {
        fixture.givenAPublished(new OrderCreatedEvent("1234", "Alice", "shirt", 2, "30$"))
                .whenPublishingA(new PaymentDoneEvent("Alice", "5555", "30$"))
                .expectDispatchedCommandsEqualTo(new UpdateStockCommand("shirt", "9876", 2));
    }

    @Test
    public void testEndSagaAfterStockUpdated() throws Exception {
        fixture.givenAPublished(new OrderCreatedEvent("1234", "Alice", "shirt", 2, "30$"))
                .andThenAPublished(new PaymentDoneEvent("Alice", "5555", "30$"))
                .whenPublishingA(new StockUpdatedEvent("shirt", "9876", 2))
                .expectNoDispatchedCommands()
                .expectActiveSagas(0);
    }
}