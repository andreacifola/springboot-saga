package service.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.SagaLifecycle;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import service.SagaOrchestratorApplication;
import service.coreapi.*;

import java.util.UUID;

@Saga
public class Change {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private String user;
    private String article;
    private int quantity;
    private String price;

    private String stockId;

    /**
     * This is the first event of the Saga, triggered when a new orderamqp is executed.
     *
     * @param event
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {

        System.out.println(repeat("-", 50) + " Order Created " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 50));
        SagaOrchestratorApplication.logger.info("Order Created " + SagaOrchestratorApplication.sagaId + "\n");

        // Saga allows us to save the state and to pass this state from a service to another
        orderId = event.getOrderId();
        user = event.getUser();
        article = event.getArticle();
        quantity = event.getQuantity();
        price = event.getPrice();

        // Associate the other services with an Id for all the Saga transaction
        String paymentId = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("paymentId", paymentId);
        stockId = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("stockId", stockId);

        System.out.println("\n" + repeat("-", 49) + " Execute Payment " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 49));
        SagaOrchestratorApplication.logger.info("Execute Payment " + SagaOrchestratorApplication.sagaId + "\n");
        commandGateway.send(new DoPaymentCommand(SagaOrchestratorApplication.accountId,
                event.getUser(), paymentId, event.getPrice()));
    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentEnabledEvent event) {
        System.out.println("\nAccount Id =                                   " + event.getAccountId());
        System.out.println("Username =                                     " + event.getUser());
        System.out.println("Money subtracted due to the ordered article =  " + event.getAmount());

        System.out.println("\n" + repeat("-", 49) + " Payment Executed " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 49));
        SagaOrchestratorApplication.logger.info("Payment Executed " + SagaOrchestratorApplication.sagaId + "\n");

        System.out.println("\n" + repeat("-", 51) + " Update Stock " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 51));
        SagaOrchestratorApplication.logger.info("Update Stock " + SagaOrchestratorApplication.sagaId + "\n");
        commandGateway.send(new UpdateStockCommand(SagaOrchestratorApplication.stockId, article, stockId, quantity));
    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentRefundedEvent event) {
        System.out.println(repeat("-", 59) + " Abort Payment : Not enough money " + repeat("-", 59));
        SagaOrchestratorApplication.logger.info("Abort Payment " + SagaOrchestratorApplication.sagaId + "\n");
        System.out.println("\n" + repeat("-", 65) + " Compensate the Order " + repeat("-", 65));

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
    public void on(SagaEndedOrderEvent event) {
        System.out.println("\n" + repeat("-", 53) + " End Saga " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 53));

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(TheSagaEndedEvent event) {
        System.out.println("\n" + repeat("-", 53) + " End Saga " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 53));

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "stockId")
    public void on(StockEndedSagaEvent event) {
        System.out.println("\nArticle Id =                                   " + event.getArticleId());
        System.out.println("Article =                                      " + event.getArticle());
        System.out.println("Quantity of the ordered article =              " + event.getQuantity());
        System.out.println("\n" + repeat("-", 53) + " End Saga " + SagaOrchestratorApplication.sagaId + " " + repeat("-", 53));

    }

    private StringBuilder repeat(String string, Integer value) {
        StringBuilder repeatedString = new StringBuilder(string);
        for (int i = 0; i < value - 1; i++) {
            repeatedString.append(string);
        }
        return repeatedString;
    }
}
