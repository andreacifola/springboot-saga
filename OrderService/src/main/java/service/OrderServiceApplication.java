package service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import service.coreapi.CreateOrderCommand;

import java.util.UUID;


@SpringBootApplication
public class  OrderServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext config = SpringApplication.run(OrderServiceApplication.class, args);
        CommandGateway commandGateway = config.getBean(CommandGateway.class);
        String orderId = UUID.randomUUID().toString();
        commandGateway.send(new CreateOrderCommand(orderId, "Alice", "shirt", 2, "30$"));
    }
}
