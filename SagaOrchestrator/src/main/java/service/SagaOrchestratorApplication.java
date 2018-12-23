package service;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import service.coreapi.*;
import service.logging.LogFile;
import java.util.UUID;
import java.util.logging.Logger;

import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

@SpringBootApplication
public class SagaOrchestratorApplication {

    public static String sagaId;
    public static String orderId;
    public static String accountId;
    public static String stockId;

    public static Logger logger = LogFile.writeLogFile();

    public static void main(String[] args) {

        ConfigurableApplicationContext config = SpringApplication.run(SagaOrchestratorApplication.class, args);
        CommandBus commandBus = config.getBean(CommandBus.class);
        CommandGateway commandGateway = config.getBean(CommandGateway.class);

        sagaId = UUID.randomUUID().toString();
        orderId = UUID.randomUUID().toString();
        accountId = UUID.randomUUID().toString();
        stockId = UUID.randomUUID().toString();

        System.out.println("\nSaga:    " + sagaId);
        System.out.println("Order:   " + orderId);
        System.out.println("Order:   " + accountId);
        System.out.println("Stock:   " + stockId);

        //dispatchSingleServices(commandBus, orderId, accountId, stockId);
        //sendSagaServices(commandGateway, orderId, accountId, stockId);
    }

    private static void dispatchSingleServices(CommandBus commandBus, String orderId, String accountId, String stockId) {

        commandBus.dispatch(asCommandMessage(new StartSagaCommand(orderId, "Alice", "shirt", 2, "30$")));
        commandBus.dispatch(asCommandMessage(new DeleteOrderCommand(orderId, "Alice", "shirt", 2, "30$")));
        commandBus.dispatch(asCommandMessage(new DoPaymentCommand(accountId, "Alice", "5555", "30$")));
        commandBus.dispatch(asCommandMessage(new RefundPaymentCommand(accountId, "Alice", "5555", "30$")));
        commandBus.dispatch(asCommandMessage(new UpdateStockCommand(stockId, "shirt", "9876", 2)));
    }

    private static void sendSagaServices(CommandGateway commandGateway, String orderId, String accountId, String articleId) {

        System.out.println("\n--------------------------------------------------- Start Saga " +
                sagaId + " ----------------------------------------------------");
        logger.info("Start Saga " + sagaId + "\n");
        System.out.println("\n-------------------------------------------------- Create Order " +
                sagaId + " ---------------------------------------------------");
        logger.info("Create Order " + sagaId + "\n");

        commandGateway.send(new StartSagaCommand(orderId, "Alice", "shirt", 2, "30$"));

        System.out.println("\n---------------------------------------------------- End Saga " +
                sagaId + " -----------------------------------------------------");
        SagaOrchestratorApplication.logger.info("End Saga " + SagaOrchestratorApplication.sagaId + "\n\n\n");
    }
}
