package servicesagaorchestrator.payment;

import com.example.demo.coreapi.DoPaymentCommand;
import com.example.demo.coreapi.PaymentDoneEvent;
import com.example.demo.coreapi.PaymentRefundedEvent;
import com.example.demo.coreapi.RefundPaymentCommand;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import servicesagaorchestrator.entities.BankAccountEntity;

import static org.junit.Assert.*;


public class PaymentTest {

    private FixtureConfiguration<Payment> fixture;
    private Payment payment = new Payment();

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(Payment.class);
    }

    @Test
    public void testDoPayment() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new DoPaymentCommand("Alice", "5555", "30$"))
                .expectEvents(new PaymentDoneEvent("Alice", "5555", "30$"));
    }

    @Test
    public void testMoneyWithdrawn() throws Exception {
        BankAccountEntity alice = new BankAccountEntity("Alice", "350$");
        String newMoneyAccount = payment.on(new PaymentDoneEvent("Alice", "5555", "30$"));
        alice.setMoneyAccount(newMoneyAccount);
        assertEquals("The money are withdrawn correctly!", "320$", alice.getMoneyAccount());
    }

    @Test
    public void testWrongPayment() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new DoPaymentCommand("Alice", "5555", "630$"))
                .expectNoEvents()
                .expectException(NotEnoughMoneyAccountException.class);
    }

    @Test
    public void testRefundPayment() throws Exception {
        fixture.given(new PaymentDoneEvent("Alice", "5555", "30$"))
                .when(new RefundPaymentCommand("Alice", "5555", "30$"))
                .expectEvents(new PaymentRefundedEvent("Alice", "5555", "30$"));
    }

    @Test
    public void testMoneyRefunded() throws Exception {
        BankAccountEntity alice = new BankAccountEntity("Alice", "350$");
        String newMoneyAccount = payment.on(new PaymentRefundedEvent("Alice", "5555", "30$"));
        alice.setMoneyAccount(newMoneyAccount);
        assertEquals("The money are withdrawn correctly!", "380$", alice.getMoneyAccount());
    }
}