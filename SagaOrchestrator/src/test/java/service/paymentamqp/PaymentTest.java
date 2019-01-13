package service.paymentamqp;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import service.coreapi.*;
import service.payment.Payment;


public class PaymentTest {

    private FixtureConfiguration<Payment> fixture; //version 3.4.1

    @Before
    public void setUp() throws Exception {
        fixture = new AggregateTestFixture<>(Payment.class); //version 3.4.1
    }

    @Test
    public void testTriggerPayment() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new TriggerPaymentCommand("1sd3gg54", "5555", "Alice", "30$"))
                .expectEvents(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"));
    }

    @Test
    public void testEnableStockUpdate() throws Exception {
        fixture.given(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .when(new EnableStockUpdateCommand("1sd3gg54", "5555", "Alice", "30$"))
                .expectEvents(new StockUpdateEnabledEvent("1sd3gg54", "5555", "Alice", "30$"));
    }

    @Test
    public void testDoPayment() throws Exception {
        fixture.given(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .when(new DoPaymentCommand("1sd3gg54", "5555", "Alice", "30$"))
                .expectEvents(new PaymentDoneEvent("1sd3gg54", "5555", "Alice", "30$"));
    }

    @Test
    public void testCompensateOrder() throws Exception {
        fixture.given(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .when(new TriggerCompensateOrderCommand("1sd3gg54", "5555", "Alice", "30$"))
                .expectEvents(new OrderCompensateTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"));
    }

    @Test
    public void testAbortPayment() throws Exception {
        fixture.given(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .when(new AbortPaymentCommand("1sd3gg54", "5555", "Alice", "30$"))
                .expectEvents(new PaymentAbortedEvent("1sd3gg54", "5555", "Alice", "30$"));
    }

    @Test
    public void testTriggerEndSagaPayment() throws Exception {
        fixture.given(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .when(new TriggerEndSagaPaymentCommand("1sd3gg54", "5555", "Alice", "30$"))
                .expectEvents(new EndSagaPaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"));
    }

    @Test
    public void testRefundPayment() throws Exception {
        fixture.given(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .when(new RefundPaymentCommand("1sd3gg54", "5555", "Alice", "30$"))
                .expectEvents(new PaymentRefundedEvent("1sd3gg54", "5555", "Alice", "30$"));
    }

    @Test
    public void testEndSagaPayment() throws Exception {
        fixture.given(new PaymentTriggeredEvent("1sd3gg54", "5555", "Alice", "30$"))
                .when(new EndSagaPaymentCommand("1sd3gg54", "5555", "Alice", "30$"))
                .expectEvents(new SagaPaymentEndedEvent("1sd3gg54", "5555", "Alice", "30$"));
    }
}