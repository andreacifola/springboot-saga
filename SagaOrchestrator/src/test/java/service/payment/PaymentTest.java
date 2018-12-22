package service.payment;

import service.coreapi.DoPaymentCommand;
import service.coreapi.PaymentDoneEvent;
import service.coreapi.PaymentRefundedEvent;
import service.coreapi.RefundPaymentCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import service.entities.BankAccountEntity;

import static org.junit.Assert.*;


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
                .when(new DoPaymentCommand("1sd3gg54", "Alice", "5555", "30$"))
                .expectEvents(new PaymentDoneEvent("1sd3gg54", "Alice", "5555", "30$"));
    }

    @Test
    public void testMoneyWithdrawn() throws Exception {
        BankAccountEntity alice = new BankAccountEntity("asdf35g55", "Alice", "350$");
        String newMoneyAccount = payment.on(new PaymentDoneEvent("1sd3gg54", "Alice", "5555", "30$"));
        alice.setMoneyAccount(newMoneyAccount);
        assertEquals("The money are withdrawn correctly!", "320$", alice.getMoneyAccount());
    }

    @Test
    public void testWrongPayment() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new DoPaymentCommand("1sd3gg54", "Alice", "5555", "630$"))
                .expectNoEvents()
                .expectException(NotEnoughMoneyAccountException.class);
    }

    @Test
    public void testRefundPayment() throws Exception {
        fixture.given(new PaymentDoneEvent("1sd3gg54", "Alice", "5555", "30$"))
                .when(new RefundPaymentCommand("1sd3gg54", "Alice", "5555", "30$"))
                .expectEvents(new PaymentRefundedEvent("1sd3gg54", "Alice", "5555", "30$"));
    }

    @Test
    public void testMoneyRefunded() throws Exception {
        BankAccountEntity alice = new BankAccountEntity("asdf35g55", "Alice", "350$");
        String newMoneyAccount = payment.on(new PaymentRefundedEvent("1sd3gg54",  "Alice", "5555", "30$"));
        alice.setMoneyAccount(newMoneyAccount);
        assertEquals("The money are withdrawn correctly!", "380$", alice.getMoneyAccount());
    }
}