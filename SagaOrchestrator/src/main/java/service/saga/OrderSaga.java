package service.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import service.SagaOrchestratorApplication;
import service.coreapi.OrderDeletedEvent;
import service.coreapi.StockUpdateTriggeredEvent;


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
     * This is the first event of the Saga, triggered when a new orderamqp is executed.
     * @param event
     */
    /*
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(SagaStartedEvent event) {
        System.out.println("-------------------------------------------------- Order Created " +
                SagaOrchestratorApplication.sagaId + " --------------------------------------------------");
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


        System.out.println("\n------------------------------------------------- Execute Payment " +
                SagaOrchestratorApplication.sagaId + " -------------------------------------------------");
        SagaOrchestratorApplication.logger.info("Execute Payment " + SagaOrchestratorApplication.sagaId + "\n");
        // We try to send the paymentamqp command...
        commandGateway.send(new TriggerPaymentCommand(SagaOrchestratorApplication.accountId,
                event.getUser(), paymentId, event.getPrice()), new CommandCallback<TriggerPaymentCommand, Object>() {
            @Override
            public void onSuccess(CommandMessage<? extends TriggerPaymentCommand> commandMessage, Object o) {
                System.out.println("------------------------------------------------ Payment Executed " +
                        SagaOrchestratorApplication.sagaId + " -------------------------------------------------");
                SagaOrchestratorApplication.logger.info("Payment Executed " +
                        SagaOrchestratorApplication.sagaId + "\n");

            }

            @Override
            public void onFailure(CommandMessage<? extends TriggerPaymentCommand> commandMessage, Throwable throwable) {
                // ... But if something goes wrong with the paymentamqp, we have to compensate the orderamqp command done before
                System.out.println("----------------------------------------------------------- " +
                        "Abort Payment : Not enough money -----------------------------------------------------------");
                SagaOrchestratorApplication.logger.info("Abort Payment " +
                        SagaOrchestratorApplication.sagaId + "\n");
                System.out.println("\n----------------------------------------------------------------- " +
                        "Compensate the Order -----------------------------------------------------------------");

                commandGateway.send(new DeleteOrderCommand(event.getOrderId(),
                        event.getUser(), event.getArticle(), event.getQuantity(), event.getPrice()));
                System.out.println("------------------------------------------------------------------- " +
                        "Order Compensated ------------------------------------------------------------------");
                SagaOrchestratorApplication.logger.info("Order Compensated " +
                        SagaOrchestratorApplication.sagaId + "\n");
            }
        });
    }
    */


    /**
     * This is the second step of the Saga; when we complete the paymentamqp, we start with the updating stockamqp command.
     * @param event
     */
    /*
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentTriggeredEvent event) {

        System.out.println("\n-------------------------------------------------- Update Stock " +
                SagaOrchestratorApplication.sagaId + " ---------------------------------------------------");
        SagaOrchestratorApplication.logger.info("Update Stock " + SagaOrchestratorApplication.sagaId + "\n");
        // We try to send the updating stockamqp command...
        commandGateway.send(new TriggerStockUpdateCommand(SagaOrchestratorApplication.stockId, article, stockId, quantity),
                new CommandCallback<TriggerStockUpdateCommand, Object>() {
            @Override
            public void onSuccess(CommandMessage<? extends TriggerStockUpdateCommand> commandMessage, Object o) {

                System.out.println("-------------------------------------------------- Stock Updated " +
                        SagaOrchestratorApplication.sagaId + " --------------------------------------------------");
                SagaOrchestratorApplication.logger.info("Stock Updated " +
                        SagaOrchestratorApplication.sagaId + "\n");
            }

            @Override
            public void onFailure(CommandMessage<? extends TriggerStockUpdateCommand> commandMessage, Throwable throwable) {

                // ... But if something goes wrong with the paymentamqp, we have to
                //     compensate both the paymentamqp command and the orderamqp command done before
                System.out.println("--------------------------------------------- Abort Stock : " +
                        "Not enough articles in the stockamqp  --------------------------------------------");
                SagaOrchestratorApplication.logger.info("Abort Stock " +
                        SagaOrchestratorApplication.sagaId + "\n");
                System.out.println("\n------------------------------------------------------ " +
                        "Compensate Payment and Order ------------------------------------------------------");

                commandGateway.send(new TriggerCompensateOrderCommand(event.getAccountId(),
                        event.getUser(), event.getPaymentId(), event.getAmount()));

                System.out.println("----------------------------------------------------------- " +
                        "Payment Compensated ----------------------------------------------------------");
                SagaOrchestratorApplication.logger.info("Payment Compensated " +
                        SagaOrchestratorApplication.sagaId + "\n");

                commandGateway.send(new DeleteOrderCommand(orderId, user, article, quantity, price));

                System.out.println("------------------------------------------------------------ " +
                        "Order Compensated -----------------------------------------------------------");
                SagaOrchestratorApplication.logger.info("Order Compensated " +
                        SagaOrchestratorApplication.sagaId + "\n");
            }
        });
    }
    */

    /**
     * This method will end the Saga when everithing ended good.
     * @param event
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "stockId")
    public void on(StockUpdateTriggeredEvent event) {
        System.out.println("\n---------------------------------------------------- End Saga " +
                SagaOrchestratorApplication.sagaId + " -----------------------------------------------------");
        //SagaLifecycle.end(); //Add this line of code if we have to conditionally end the saga
    }

    /**
     * This method will anyway end the Saga, but when something went wrong.
     * @param event
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderDeletedEvent event) {
        System.out.println("\n---------------------------------------------------- End Saga " +
                SagaOrchestratorApplication.sagaId + " -----------------------------------------------------");

    }
}
