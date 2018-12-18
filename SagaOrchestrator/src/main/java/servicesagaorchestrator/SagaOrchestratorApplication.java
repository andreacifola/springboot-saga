package servicesagaorchestrator;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import servicesagaorchestrator.coreapi.*;

import java.util.UUID;

import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

@SpringBootApplication
//@EntityScan(basePackages = {"the package on which it depends"}) //Add this only if it realy depends on the SagaOrchestrator module
//@ComponentScan(basePackages = {"the package on which it depends"}) //Add this only if it realy depends on the SagaOrchestrator module
public class SagaOrchestratorApplication {

    public static String sagaId;
    public static String orderId;
    public static String accountId;
    public static String stockId;

    public static void main(String[] args) {

        ConfigurableApplicationContext config = SpringApplication.run(SagaOrchestratorApplication.class, args);
        CommandBus commandBus = config.getBean(CommandBus.class);
        CommandGateway commandGateway = config.getBean(CommandGateway.class); //This does not work!

        sagaId = UUID.randomUUID().toString();
        orderId = UUID.randomUUID().toString();
        accountId = UUID.randomUUID().toString();
        stockId = UUID.randomUUID().toString();

        dispatchSingleServices(commandBus, orderId, accountId, stockId);
        sendSagaServices(commandGateway, orderId, accountId, stockId);
    }

    private static void dispatchSingleServices(CommandBus commandBus, String orderId, String accountId, String stockId) {

        commandBus.dispatch(asCommandMessage(new CreateOrderCommand(orderId, "Alice", "shirt", 2, "30$")));
        commandBus.dispatch(asCommandMessage(new DeleteOrderCommand(orderId, "Alice", "shirt", 2, "30$")));
        commandBus.dispatch(asCommandMessage(new DoPaymentCommand(accountId, "Alice", "5555", "30$")));
        //commandBus.dispatch(asCommandMessage(new RefundPaymentCommand(accountId, "Alice", "5555", "30$")));
        commandBus.dispatch(asCommandMessage(new UpdateStockCommand(stockId, "shirt", "9876", 2)));
    }


    private static void sendSagaServices(CommandGateway commandGateway, String orderId, String accountId, String articleId) {

        System.out.println("\n------------------------------------- Start Saga " + sagaId + " --------------------------------------");
        System.out.println("\n------------------------------------ Create Order " + sagaId + " -------------------------------------");
        commandGateway.send(new CreateOrderCommand(orderId, "Alice", "shirt", 2, "30$"));
        //commandGateway.send(new DoPaymentCommand(accountId,"Alice", "5555", "30$"));
        //commandGateway.send(new UpdateStockCommand(articleId, "shirt", "9876", 2));
        System.out.println("\n-------------------------------------- End Saga " + sagaId + " ---------------------------------------");
    }

    @Bean
    public EventStorageEngine eventStorageEngine() {
        return new InMemoryEventStorageEngine();
    }

	/*
	@Bean
	public CommandBus commandBus() {
		return new AsynchronousCommandBus();
	}
	*/
}
