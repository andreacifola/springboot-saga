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
     * This is the first event of the Saga, triggered when a new order is executed.
     *
     * @param event
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(SagaStartedEvent event) {

        System.out.println(repeat("-", 50) + " Order Created " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 50));
        SagaOrchestratorApplication.logger.info("Order Created " + SagaOrchestratorApplication.sagaId + "\n");

        // Saga allows us to save the state and to pass this state from a service to another
        orderId = event.getOrderId();
        user = event.getUser();
        article = event.getArticle();
        quantity = event.getQuantity();
        price = event.getPrice();

        // Associate the other services with an Id for all the Saga transaction
        paymentId = UUID.randomUUID().toString();
        associateWith("paymentId", paymentId);
        stockId = UUID.randomUUID().toString();
        associateWith("stockId", stockId);

        System.out.println("\n" + repeat("-", 49) + " Execute Payment " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 49));
        SagaOrchestratorApplication.logger.info("Execute Payment " + SagaOrchestratorApplication.sagaId + "\n");
        commandGateway.send(new TriggerPaymentCommand(SagaOrchestratorApplication.accountId, event.getUser(), paymentId, event.getPrice()));
    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(StockUpdateEnabledEvent event) {
        this.accountId = event.getAccountId();
        this.amount = event.getAmount();
        System.out.println("\nAccount Id =" + repeat(" ", 35) + event.getAccountId());
        System.out.println("Username =" + repeat(" ", 37) + event.getUser());
        System.out.println("Money subtracted due to the ordered article =  " + event.getAmount());

        System.out.println("\n" + repeat("-", 49) + " Payment Executed " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 49));
        SagaOrchestratorApplication.logger.info("Payment Executed " + SagaOrchestratorApplication.sagaId + "\n");

        System.out.println("\n" + repeat("-", 51) + " Update Stock " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 51));
        SagaOrchestratorApplication.logger.info("Update Stock " + SagaOrchestratorApplication.sagaId + "\n");
        commandGateway.send(new TriggerStockUpdateCommand(SagaOrchestratorApplication.stockId, article, stockId, quantity));
    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(OrderCompensateTriggeredEvent event) {
        System.out.println(repeat("-", 59) + " Abort Payment : Not enough money " + repeat("-", 59));
        SagaOrchestratorApplication.logger.info("Abort Payment " + SagaOrchestratorApplication.sagaId + "\n");
        System.out.println("\n" + repeat("-", 65) + " Compensate the Order " + repeat("-", 65));

        commandGateway.send(new DeleteOrderCommand(orderId, user, article, quantity, price));
        System.out.println(repeat("-", 67) + " Order Compensated " + repeat("-", 67));
        SagaOrchestratorApplication.logger.info("Order Compensated " + SagaOrchestratorApplication.sagaId + "\n");
    }

    @SagaEventHandler(associationProperty = "stockId")
    public void on(CompensatePaymentTriggeredEvent event) {
        System.out.println(repeat("-", 59) + " Abort Stock : Not enough articles " + repeat("-", 59));
        SagaOrchestratorApplication.logger.info("Abort Stock " + SagaOrchestratorApplication.sagaId + "\n");
        System.out.println("\n" + repeat("-", 57) + " Compensate the Payment and the Order " + repeat("-", 57));

        commandGateway.send(new RefundPaymentCommand(accountId, user, paymentId, amount));
        System.out.println(repeat("-", 66) + " Payment Compensated " + repeat("-", 66));
        SagaOrchestratorApplication.logger.info("Payment Compensated " + SagaOrchestratorApplication.sagaId + "\n");

        commandGateway.send(new DeleteOrderCommand(orderId, user, article, quantity, price));
        System.out.println(repeat("-", 67) + " Order Compensated " + repeat("-", 67));
        SagaOrchestratorApplication.logger.info("Order Compensated " + SagaOrchestratorApplication.sagaId + "\n");
    }


    /**
     * This method will anyway end the Saga, but when something went wrong.
     *
     * @param event
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(EndSagaOrderTriggeredEvent event) {
        System.out.println("\n" + repeat("-", 53) + " End Saga " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 53) + "\n\n");
        SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n");

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(SagaOrderEndedEvent event) {
        isOrderCompensated = true;
        if (isPaymentCompensated == null || isPaymentCompensated) {
            System.out.println("\n" + repeat("-", 53) + " End Saga " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 53) + "\n\n");
            SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n");
            end();
        }

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "stockId")
    public void on(StockSagaEndedEvent event) {
        System.out.println("\nArticle Id =" + repeat(" ", 35) + event.getArticleId());
        System.out.println("Article =" + repeat(" ", 38) + event.getArticle());
        System.out.println("Quantity of the ordered article =" + repeat(" ", 14) + event.getQuantity());
        System.out.println("\n" + repeat("-", 51) + " Stock Updated " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 50) + "\n");
        System.out.println("\n" + repeat("-", 53) + " End Saga " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 53) + "\n\n");
        SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n");

    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(SagaPaymentEndedEvent event) {
        isPaymentCompensated = true;
        if (isOrderCompensated) {
            System.out.println("\n" + repeat("-", 53) + " End Saga " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 53) + "\n\n");
            SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n");
            end();
        }
    }

    private StringBuilder repeat(String string, Integer value) {
        StringBuilder repeatedString = new StringBuilder(string);
        for (int i = 0; i < value - 1; i++) {
            repeatedString.append(string);
        }
        return repeatedString;
    }
}
