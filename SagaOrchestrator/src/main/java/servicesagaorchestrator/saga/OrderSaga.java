package servicesagaorchestrator.saga;


import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.SagaLifecycle;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import servicesagaorchestrator.SagaOrchestratorApplication;
import servicesagaorchestrator.coreapi.*;

import java.util.UUID;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private String user;
    private String article;
    private int quantity;
    private String price;

    private String stockId;

    /**
     * This is the first event of the Saga, triggered when a new order is executed.
     * @param event
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        System.out.println("------------------------------------ Order Created " + SagaOrchestratorApplication.sagaId + " ------------------------------------");
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

        // We try to send the payment command...
        try {
            System.out.println("\n----------------------------------- Execute Payment " + SagaOrchestratorApplication.sagaId + " -----------------------------------");
            commandGateway.sendAndWait(new DoPaymentCommand(SagaOrchestratorApplication.orderId, event.getUser(), paymentId, event.getPrice()));
            System.out.println("---------------------------------- Payment Executed " + SagaOrchestratorApplication.sagaId + " -----------------------------------");

        } catch (Exception e) {

            // ... But if something goes wrong with the payment, we have to compensate the order command done before
            if (e.getCause() instanceof NotEnoughMoneyAccountException) {
                System.out.println("-------------------- Not enough money: Compensate the Order -------------------");
                commandGateway.send(new DeleteOrderCommand(event.getOrderId(),
                        event.getUser(), event.getArticle(), event.getQuantity(), event.getPrice()));
                System.out.println("------------------------------- Order Compensated -----------------------------");
            }
        }
    }

    /**
     * This is the second step of the Saga; when we complete the payment, we start with the updating stock command.
     * @param event
     */
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentDoneEvent event) {

        // We try to send the updating stock command...
        try {
            System.out.println("\n------------------------------------ Update Stock " + SagaOrchestratorApplication.sagaId + " ------------------------------------");
            commandGateway.sendAndWait(new UpdateStockCommand(SagaOrchestratorApplication.stockId, article, stockId, quantity));
            System.out.println("------------------------------------ Stock Updated " + SagaOrchestratorApplication.sagaId + " ------------------------------------");

        } catch (CommandExecutionException e) {

            // ... But if something goes wrong with the payment, we have to
            //     compensate both the payment command and the order command done before
            if (e.getCause() instanceof NotEnoughMoneyAccountException) {
                System.out.println("--------- Not enough articles in the stock : Compensate Payment and Order ---------");

                commandGateway.send(new RefundPaymentCommand(SagaOrchestratorApplication.accountId, event.getUser(), event.getPaymentId(), event.getAmount()));
                System.out.println("------------------------------- Payment Compensated -------------------------------");

                commandGateway.send(new DeleteOrderCommand(orderId, user, article, quantity, price));
                System.out.println("-------------------------------- Order Compensated --------------------------------");
            }
        }
    }

    /**
     * This method will end the Saga when everithing ended good.
     * @param event
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "stockId")
    public void on(StockUpdatedEvent event) {
        //SagaLifecycle.end(); //Add this line of code if we have to conditionally end the saga
    }

    /**
     * This method will anyway end the Saga, but when something went wrong.
     * @param event
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderDeletedEvent event) {

    }
}
