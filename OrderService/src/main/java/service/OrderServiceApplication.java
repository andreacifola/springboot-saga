package service;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import service.coreapi.CreateOrderCommand;


@SpringBootApplication
public class  OrderServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext config = SpringApplication.run(OrderServiceApplication.class, args);
        CommandGateway commandGateway = config.getBean(CommandGateway.class);
        commandGateway.send(new CreateOrderCommand("dahdeiwuh3289", "Alice", "shirt", 2, "30$"));
    }
}
