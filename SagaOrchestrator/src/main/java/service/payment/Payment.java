package service.payment;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import service.coreapi.*;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Payment {

    @AggregateIdentifier
    private String accountId;
    private String user;
    private String amount;

    public Payment() {

    }

    @CommandHandler
    public Payment(TriggerPaymentCommand command) {
        apply(new PaymentTriggeredEvent(command.getPaymentId(),
                command.getAccountId(), command.getUser(), command.getAmount()));
    }

    @CommandHandler
    public void handle(EnableStockUpdateCommand command) {
        apply(new StockUpdateEnabledEvent(command.getPaymentId(),
                command.getAccountId(), command.getUser(), command.getAmount()));
    }

    @CommandHandler
    public void handle(DoPaymentCommand command) {
        apply(new PaymentDoneEvent(command.getPaymentId(),
                command.getAccountId(), command.getUser(), command.getAmount()));
    }

    @CommandHandler
    public void handle(TriggerCompensateOrderCommand command) {
        apply(new OrderCompensateTriggeredEvent(command.getPaymentId(),
                command.getAccountId(), command.getUser(), command.getAmount()));
    }

    @CommandHandler
    public void handle(AbortPaymentCommand command) {
        apply(new PaymentAbortedEvent(command.getPaymentId(),
                command.getAccountId(), command.getUser(), command.getAmount()));
    }

    @CommandHandler
    public void handle(TriggerEndSagaPaymentCommand command) {
        apply(new EndSagaPaymentTriggeredEvent(command.getPaymentId(),
                command.getAccountId(), command.getUser(), command.getAmount()));
    }

    @CommandHandler
    public void handle(RefundPaymentCommand command) {
        apply(new PaymentRefundedEvent(command.getPaymentId(),
                command.getAccountId(), command.getUser(), command.getAmount()));
    }

    @CommandHandler
    public void handle(EndSagaPaymentCommand command) {
        apply(new SagaPaymentEndedEvent(command.getPaymentId(),
                command.getAccountId(), command.getUser(), command.getAmount()));
    }



    @EventSourcingHandler
    public void on(PaymentTriggeredEvent event) {
        this.accountId = event.getPaymentId();
        this.user = event.getUser();
        this.amount = event.getAmount();
    }

    @EventSourcingHandler
    public void on(OrderCompensateTriggeredEvent event) {

    }

    @EventSourcingHandler
    public void on(PaymentAbortedEvent event) {

    }

    @EventSourcingHandler
    public void on(StockUpdateEnabledEvent event) {

    }

    @EventSourcingHandler
    public void on(PaymentDoneEvent event) {

    }

    @EventSourcingHandler
    public void on(EndSagaPaymentTriggeredEvent event) {

    }

    @EventSourcingHandler
    public void on(PaymentRefundedEvent event) {
        System.out.println("\nPayment Id =      " + event.getPaymentId());
        System.out.println("Username =        " + event.getUser());
        System.out.println("Money refunded =  " + event.getAmount() + "\n");

    }

    @EventSourcingHandler
    public void on(SagaPaymentEndedEvent event) {

    }
}
