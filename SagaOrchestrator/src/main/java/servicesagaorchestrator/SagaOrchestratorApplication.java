package servicesagaorchestrator;

import com.example.demo.coreapi.*;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.spring.config.EnableAxonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

@SpringBootApplication
@EnableAxonAutoConfiguration
//@EntityScan(basePackages = {"the package on which it depends"}) //Add this only if it realy depends on the SagaOrchestrator module
//@ComponentScan(basePackages = {"the package on which it depends"}) //Add this only if it realy depends on the SagaOrchestrator module
public class SagaOrchestratorApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext config = SpringApplication.run(SagaOrchestratorApplication.class, args);
        CommandBus commandBus = config.getBean(CommandBus.class);
        //CommandGateway commandGateway = config.getBean(CommandGateway.class);

        dispatchSingleServices(commandBus);
        //sendSagaServices(commandGateway);
    }

	/*private static void sendSagaServices(CommandGateway commandGateway) {

		commandGateway.send(new CreateOrderCommand("1234", "Alice", "shirt", 2, "30$"));
		commandGateway.send(new DoPaymentCommand("Alice", "5555", "30$"));
		commandGateway.send(new UpdateStockCommand("shirt", "9876", 2));
	}*/

    private static void dispatchSingleServices(CommandBus commandBus) {

        commandBus.dispatch(asCommandMessage(new CreateOrderCommand("1234", "Alice", "shirt", 2, "30$")));
        commandBus.dispatch(asCommandMessage(new DeleteOrderCommand("1234", "Alice", "shirt", 2, "30$")));
        commandBus.dispatch(asCommandMessage(new DoPaymentCommand("Alice", "5555", "30$")));
        commandBus.dispatch(asCommandMessage(new RefundPaymentCommand("Alice", "5555", "30$")));
        commandBus.dispatch(asCommandMessage(new UpdateStockCommand("shirt", "9876", 2)));
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
