package service.payment;

import service.coreapi.DoPaymentCommand;
import service.coreapi.PaymentDoneEvent;
import service.coreapi.PaymentRefundedEvent;
import service.coreapi.RefundPaymentCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import service.SagaOrchestratorApplication;
import service.entities.BankAccountEntity;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Payment {

    @AggregateIdentifier
    private String accountId;
    private String user;
    private String amount;

    private BankAccountEntity alice = new BankAccountEntity(SagaOrchestratorApplication.accountId, "Alice", "350$");

    public Payment() {

    }

    @CommandHandler
    public Payment(DoPaymentCommand command) {
        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));
        Integer price = Integer.valueOf(command.getAmount().substring(0, command.getAmount().length()-1));

        if (moneyAccount >= price) {
            apply(new PaymentDoneEvent(command.getAccountId(), command.getUser(), command.getPaymentId(), command.getAmount()));
        } else {
            System.out.println("\nYou don't have enough money in your bank account!\n");
            throw new NotEnoughMoneyAccountException();
        }
    }

    @CommandHandler
    public void handle(RefundPaymentCommand command) {
        apply(new PaymentRefundedEvent(accountId, user, command.getPaymentId(), command.getAmount()));
    }

    @EventSourcingHandler
    public String on(PaymentDoneEvent event) {
        this.accountId = event.getAccountId();
        this.user = event.getUser();
        this.amount = event.getAmount();

        System.out.println("\nAccount Id =                    " + event.getAccountId());
        System.out.println("Username =                      " + event.getUser());
        System.out.println(event.getUser() + " money account =           " + alice.getMoneyAccount());
        System.out.println("Price of the ordered article =  " + event.getAmount());

        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));
        moneyAccount -= price;
        alice.setMoneyAccount(moneyAccount.toString()+"$");

        System.out.println("Alice new money account =       " + alice.getMoneyAccount() +"\n");
        return alice.getMoneyAccount();
    }

    @EventSourcingHandler
    public String on(PaymentRefundedEvent event) {
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));
        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));

        System.out.println("\nAlice money account =      " + alice.getMoneyAccount());
        System.out.println("Amount to refund =         " + event.getAmount());

        moneyAccount += price;
        alice.setMoneyAccount(moneyAccount.toString()+"$");

        System.out.println("Alice new money account =  " + alice.getMoneyAccount() + "\n");
        return alice.getMoneyAccount();
    }
}
