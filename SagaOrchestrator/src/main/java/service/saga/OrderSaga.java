package service.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import service.SagaOrchestratorApplication;
import service.coreapi.*;

import java.util.UUID;

import static org.axonframework.eventhandling.saga.SagaLifecycle.associateWith;
import static org.axonframework.eventhandling.saga.SagaLifecycle.end;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private String user;
    private String article;
    private int quantity;
    private String price;

    private String accountId;
    private String amount;

    private String paymentId;
    private String stockId;

    private Boolean isPaymentCompensated = null;
    private Boolean isOrderCompensated = false;

    /**
     * This is the first event of the Saga, triggered when a new order is executed. We save in the log file the event
     * and at the end we send the TriggerPaymentCommand to the PaymentService to continue with the Saga.
     * @param event
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(SagaStartedEvent event) {

        System.out.println(repeat("-", 50) + " Order Created " +
                SagaOrchestratorApplication.sagaId + " " + repeat("-", 50));
        SagaOrchestratorApplication.logger.info("Order Created " + SagaOrchestratorApplication.sagaId + "\n");

        //Saga allows us to save the state and to pass this state from a service to another
        orderId = event.getOrderId();
        user = event.getUser();
        article = event.getArticle();
        quantity = event.getQuantity();
        price = event.getPrice();

        //Associate the other services with an Id for all the Saga transaction
        paymentId = UUID.randomUUID().toString();
        associateWith("paymentId", paymentId);
        stockId = UUID.randomUUID().toString();
        associateWith("stockId", stockId);

        System.out.println("\n" + repeat("-", 49) + " Execute Payment " +
                SagaOrchestratorApplication.sagaId + " " + repeat("-", 49));
        SagaOrchestratorApplication.logger.info("Execute Payment " + SagaOrchestratorApplication.sagaId + "\n");
        commandGateway.send(new TriggerPaymentCommand(paymentId, "", event.getUser(), event.getPrice()));
    }

    /**
     * This event is sent by the PaymentService when its transaction goes well. We save in the log file the event
     * and at the end we send the TriggerStockUpdateCommand to the StockService to continue with the Saga.
     *
     * @param event
     */
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(StockUpdateEnabledEvent event) {
        this.accountId = event.getAccountId();
        this.amount = event.getAmount();
        System.out.println("\nPayment Id =" + repeat(" ", 35) + event.getPaymentId());
        System.out.println("Username =" + repeat(" ", 37) + event.getUser());
        System.out.println("Money subtracted due to the ordered article =  " + event.getAmount());

        System.out.println("\n" + repeat("-", 49) + " Payment Executed " +
                SagaOrchestratorApplication.sagaId + " " + repeat("-", 49));
        SagaOrchestratorApplication.logger.info("Payment Executed " +
                SagaOrchestratorApplication.sagaId + "\n");

        System.out.println("\n" + repeat("-", 51) + " Update Stock " +
                SagaOrchestratorApplication.sagaId + " " + repeat("-", 51));
        SagaOrchestratorApplication.logger.info("Update Stock " + SagaOrchestratorApplication.sagaId + "\n");
        commandGateway.send(new TriggerStockUpdateCommand(stockId, "", article, quantity));
    }

    /**
     * This event is sent by the PaymentService when its transaction goes wrong. So we need to start the
     * compensating action in the OrderService. We save the event in the log file and at the end we send
     * the DeleteOrderCommand to the OrderService to end with the Saga.
     * @param event
     */
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(OrderCompensateTriggeredEvent event) {
        System.out.println(repeat("-", 59) +
                " Abort Payment : Not enough money " + repeat("-", 59));
        SagaOrchestratorApplication.logger.info("Abort Payment " + SagaOrchestratorApplication.sagaId + "\n");
        System.out.println("\n" + repeat("-", 65) +
                " Compensate the Order " + repeat("-", 65));

        commandGateway.send(new DeleteOrderCommand(orderId, user, article, quantity, price));
        System.out.println("\nOrder " + orderId + " deleted.\n");
        System.out.println(repeat("-", 67) +
                " Order Compensated " + repeat("-", 67));
        SagaOrchestratorApplication.logger.info("Order Compensated " + SagaOrchestratorApplication.sagaId + "\n");
    }

    /**
     * This event is sent by the StockService when its transaction goes wrong. So we need to start the
     * compensating actions in the PaymentService and the OrderService. We save the event in the log file
     * and at the end we send the RefundPaymentCommand and the DeleteOrderCommand to the PaymentService
     * and the OrderService respectively to end with the Saga.
     * @param event
     */
    @SagaEventHandler(associationProperty = "stockId")
    public void on(CompensatePaymentTriggeredEvent event) {
        System.out.println(repeat("-", 59) +
                " Abort Stock : Not enough articles " + repeat("-", 59));
        SagaOrchestratorApplication.logger.info("Abort Stock " + SagaOrchestratorApplication.sagaId + "\n");
        System.out.println("\n" + repeat("-", 57) +
                " Compensate the Payment and the Order " + repeat("-", 57));

        commandGateway.send(new RefundPaymentCommand(paymentId, accountId, user, amount));
        System.out.println("\nPayment Id =      " + paymentId);
        System.out.println("Username =        " + user);
        System.out.println("Money refunded =  " + price + "\n");
        System.out.println(repeat("-", 66) +
                " Payment Compensated " + repeat("-", 66));
        SagaOrchestratorApplication.logger.info("Payment Compensated " + SagaOrchestratorApplication.sagaId + "\n");

        commandGateway.send(new DeleteOrderCommand(orderId, user, article, quantity, price));
        System.out.println("\nOrder " + orderId + " deleted.\n");
        System.out.println(repeat("-", 67) + " Order Compensated " + repeat("-", 67));
        SagaOrchestratorApplication.logger.info("Order Compensated " + SagaOrchestratorApplication.sagaId + "\n");
    }

    /**
     * This method will anyway end the Saga; the event is sent by the OrderService but when something went wrong.
     * So it will end the Saga once the OrderService has done with the compensating action
     * @param event
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(EndSagaOrderTriggeredEvent event) {
        System.out.println("\n" + repeat("-", 53) + " End Saga " +
                SagaOrchestratorApplication.sagaId + " " + repeat("-", 53) + "\n\n");
        SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n");

    }

    /**
     * This event is sent by the StockService and will end the saga only when both the OrderService and
     * the Payment service have done wiht their compensating actions. It writes the event in the log file.
     * @param event
     */
    @SagaEventHandler(associationProperty = "orderId")
    public void on(SagaOrderEndedEvent event) {
        isOrderCompensated = true;
        if (isPaymentCompensated == null || isPaymentCompensated) {
            System.out.println("\n" + repeat("-", 53) + " End Saga " +
                    SagaOrchestratorApplication.sagaId + " " + repeat("-", 53) + "\n\n");
            SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n");
            end();
        }

    }

    /**
     * This event is sent by the StockService and will end the saga when everything went well and
     * we haven't done the compensating actions. It writes the event in the log file.
     * @param event
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "stockId")
    public void on(StockSagaEndedEvent event) {
        System.out.println("\nStock Id =" + repeat(" ", 25) + event.getStockId());
        System.out.println("Article =" + repeat(" ", 26) + event.getArticle());
        System.out.println("Quantity of the ordered article =  " + +event.getQuantity());
        System.out.println("\n" + repeat("-", 51) + " Stock Updated " +
                SagaOrchestratorApplication.sagaId + " " + repeat("-", 50) + "\n");
        System.out.println("\n" + repeat("-", 53) + " End Saga " +
                SagaOrchestratorApplication.sagaId + " " + repeat("-", 53) + "\n\n");
        SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n");

    }

    /**
     * This event is sent by the PaymentService and will end the saga only when also the OrderService has done
     * wiht its compensating action. It writes the event in the log file.
     * @param event
     */
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(SagaPaymentEndedEvent event) {
        isPaymentCompensated = true;
        if (isOrderCompensated) {
            System.out.println("\n" + repeat("-", 53) + " End Saga "
                    + SagaOrchestratorApplication.sagaId + " " + repeat("-", 53) + "\n\n");
            SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n");
            end();
        }
    }

    /**
     * This method produces a string composed by the same value, repeated as indicated in the value field
     * @param string
     * @param value
     * @return
     */
    private StringBuilder repeat(String string, Integer value) {
        StringBuilder repeatedString = new StringBuilder(string);
        for (int i = 0; i < value - 1; i++) {
            repeatedString.append(string);
        }
        return repeatedString;
    }
}
