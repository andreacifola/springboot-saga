package service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import service.coreapi.CreateOrderCommand;
import service.database.OrderEntityRepository;

import java.util.UUID;


@SpringBootApplication
public class  OrderServiceApplication {

    private static OrderEntityRepository orderEntityRepository;

    @Autowired
    public OrderServiceApplication(OrderEntityRepository orderEntityRepository) {
        OrderServiceApplication.orderEntityRepository = orderEntityRepository;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext config = SpringApplication.run(OrderServiceApplication.class, args);
        CommandGateway commandGateway = config.getBean(CommandGateway.class);
        String orderId = UUID.randomUUID().toString();

        //TODO eliminare quando è finito
        orderEntityRepository.deleteAll();

        //Order sends the first command that starts the saga in the SagaOrchestrator
        commandGateway.send(new CreateOrderCommand(orderId, "Alice", "shirt", 2, "30$"));
    }
}
