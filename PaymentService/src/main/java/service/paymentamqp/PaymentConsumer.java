package service.paymentamqp;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import service.coreapi.*;
import service.entities.BankAccountEntity;


@ProcessingGroup("paymentEvents")
@RestController
public class PaymentConsumer {

    private final CommandGateway commandGateway;
    private BankAccountEntity alice = new BankAccountEntity("sd573jn3", "Alice", "350$");

    public PaymentConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(PaymentDoneEvent event) {
        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length() - 1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length() - 1));

        if (moneyAccount >= price) {
            System.out.println("\nAccount Id =                    " + event.getAccountId());
            System.out.println("Username =                      " + event.getUser());
            System.out.println(event.getUser() + " money account =           " + alice.getMoneyAccount());
            System.out.println("Price of the ordered article =  " + event.getAmount());

            moneyAccount -= price;
            alice.setMoneyAccount(moneyAccount.toString() + "$");
            System.out.println("Alice new money account =       " + alice.getMoneyAccount() + "\n");
            commandGateway.send(new DoPaymentCommand(event.getAccountId(), event.getUser(), event.getPaymentId(), event.getAmount()));
            commandGateway.send(new EnableStockUpdateCommand(event.getAccountId(), event.getUser(), event.getPaymentId(), event.getAmount()));
        } else {
            System.out.println("\nYou don't have enough money in your bank account!\n");
            commandGateway.send(new DoPaymentCommand(event.getAccountId(), event.getUser(), event.getPaymentId(), event.getAmount()));
            commandGateway.send(new CompensateOrderCommand(event.getAccountId(), event.getUser(), event.getPaymentId(), event.getAmount()));
        }
    }

    @EventHandler
    public void on(PaymentRefundedEvent event) {
        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));

        System.out.println("\nAlice money account =      " + alice.getMoneyAccount());
        System.out.println("Amount to refund =         " + event.getAmount());

        moneyAccount += price;
        alice.setMoneyAccount(moneyAccount.toString()+"$");

        System.out.println("Alice new money account =  " + alice.getMoneyAccount() + "\n");
        //todo send command
    }

    @Bean
    public SpringAMQPMessageSource paymentQueueMessageSource(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "Payment")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }
}
