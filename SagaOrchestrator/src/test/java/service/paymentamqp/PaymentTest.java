package service.paymentamqp;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import service.coreapi.OrderCompensateTriggeredEvent;
import service.coreapi.PaymentTriggeredEvent;
import service.coreapi.TriggerCompensateOrderCommand;
import service.coreapi.TriggerPaymentCommand;
import service.payment.NotEnoughMoneyAccountException;
import service.payment.Payment;


public class PaymentTest {

    private FixtureConfiguration<Payment> fixture; //version 3.4.1
    private Payment payment = new Payment();

    @Before
    public void setUp() throws Exception {
        fixture = new AggregateTestFixture<>(Payment.class); //version 3.4.1
    }

    @Test
    public void testDoPayment() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new TriggerPaymentCommand("1sd3gg54", "Alice", "30$"))
                .expectEvents(new PaymentTriggeredEvent("1sd3gg54", "Alice", "30$"));
    }

    /*
    @Test
    public void testMoneyWithdrawn() throws Exception {
        BankAccountEntity alice = new BankAccountEntity("asdf35g55", "Alice", "350$");
        String newMoneyAccount = payment.on(new PaymentTriggeredEvent("1sd3gg54", "Alice", "5555", "30$"));
        alice.setMoneyAccount(newMoneyAccount);
        assertEquals("The money are withdrawn correctly!", "320$", alice.getMoneyAccount());
    }
    */

    @Test
    public void testWrongPayment() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new TriggerPaymentCommand("1sd3gg54", "Alice", "630$"))
                .expectNoEvents()
                .expectException(NotEnoughMoneyAccountException.class);
    }

    @Test
    public void testRefundPayment() throws Exception {
        fixture.given(new PaymentTriggeredEvent("1sd3gg54", "Alice", "30$"))
                .when(new TriggerCompensateOrderCommand("1sd3gg54", "Alice", "30$"))
                .expectEvents(new OrderCompensateTriggeredEvent("1sd3gg54", "Alice", "30$"));
    }

    /*
    @Test
    public void testMoneyRefunded() throws Exception {
        BankAccountEntity alice = new BankAccountEntity("asdf35g55", "Alice", "350$");
        String newMoneyAccount = payment.on(new OrderCompensateTriggeredEvent("1sd3gg54",  "Alice", "5555", "30$"));
        alice.setMoneyAccount(newMoneyAccount);
        assertEquals("The money are withdrawn correctly!", "380$", alice.getMoneyAccount());
    }
    */
}