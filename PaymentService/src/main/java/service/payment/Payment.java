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
    public Payment(DoPaymentCommand command) {
        apply(new PaymentDoneEvent(command.getAccountId(), command.getUser(), command.getPaymentId(), command.getAmount()));
    }

    @CommandHandler
    public void handle(EnablePaymentCommand command) {
        apply(new PaymentEnabledEvent(command.getAccountId(), command.getUser(), command.getPaymentId(), command.getAmount()));
    }

    @CommandHandler
    public void handle(RefundPaymentCommand command) {
        apply(new PaymentRefundedEvent(command.getAccountId(), command.getUser(), command.getPaymentId(), command.getAmount()));
    }

    @CommandHandler
    public void handle(EnableStockUpdateCommand command) {
        apply(new StockUpdateEnabledEvent(command.getAccountId(), command.getUser(), command.getPaymentId(), command.getAmount()));
    }

    @CommandHandler
    public void handle(CompensateOrderCommand command) {
        apply(new OrderCompensatedEvent(command.getAccountId(), command.getUser(), command.getPaymentId(), command.getAmount()));
    }


    @EventSourcingHandler
    public void on(PaymentDoneEvent event) {
        this.accountId = event.getAccountId();
        this.user = event.getUser();
        this.amount = event.getAmount();
    }

    @EventSourcingHandler
    public void on(PaymentEnabledEvent event) {

    }

    @EventSourcingHandler
    public void on(PaymentRefundedEvent event) {

    }

    @EventSourcingHandler
    public void on(StockUpdateEnabledEvent event) {

    }

    @EventSourcingHandler
    public void on(OrderCompensatedEvent event) {

    }
}
