package servicesagaorchestrator.payment;

import com.example.demo.coreapi.DoPaymentCommand;
import com.example.demo.coreapi.PaymentDoneEvent;
import com.example.demo.coreapi.PaymentRefundedEvent;
import com.example.demo.coreapi.RefundPaymentCommand;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import servicesagaorchestrator.entities.BankAccountEntity;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
public class Payment {

    @AggregateIdentifier
    private String user;
    private String amount;

    private BankAccountEntity alice = new BankAccountEntity("Alice", "350$");

    @CommandHandler
    public Payment(DoPaymentCommand command) throws NotEnoughMoneyAccountException {
        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));
        Integer price = Integer.valueOf(command.getAmount().substring(0, command.getAmount().length()-1));

        if (moneyAccount >= price) {
            apply(new PaymentDoneEvent(command.getUser(), command.getPaymentId(), command.getAmount()));
        } else {
            System.out.println("You don't have enough money in your bank account!\n");
            throw new NotEnoughMoneyAccountException();
        }
    }

    @CommandHandler
    public void handle(RefundPaymentCommand command) {
        apply(new PaymentRefundedEvent(user, command.getPaymentId(), command.getAmount()));
    }

    @EventSourcingHandler
    public String on(PaymentDoneEvent event) {
        this.user = event.getUser();
        this.amount = event.getAmount();

        System.out.println("\nAlice money account = " + alice.getMoneyAccount());
        System.out.println("Price of the ordered article = " + event.getAmount());

        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));
        moneyAccount -= price;
        alice.setMoneyAccount(moneyAccount.toString()+"$");

        System.out.println("Alice new money account = " + alice.getMoneyAccount() + " --> PRINTED IN \"Payment.java\"\n");
        return alice.getMoneyAccount();
    }

    @EventSourcingHandler
    public String on(PaymentRefundedEvent event) {
        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));

        System.out.println("\nAlice money account = " + alice.getMoneyAccount());
        System.out.println("Amount of refund = " + event.getAmount());

        moneyAccount += price;
        alice.setMoneyAccount(moneyAccount.toString()+"$");

        System.out.println("Alice new money account = " + alice.getMoneyAccount() + " --> PRINTED IN \"Payment.java\"\n");
        return alice.getMoneyAccount();
    }
}
