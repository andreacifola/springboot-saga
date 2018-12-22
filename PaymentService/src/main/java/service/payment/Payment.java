package service.payment;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import service.coreapi.PaymentDoneEvent;
import service.coreapi.PaymentRefundedEvent;
import service.entities.BankAccountEntity;


@ProcessingGroup("paymentEvents")
@RestController
public class Payment {

    private BankAccountEntity alice = new BankAccountEntity("sd573jn3", "Alice", "350$");

    @EventHandler
    public String on(PaymentDoneEvent event) {

        System.out.println("\nAccount Id =                    " + event.getAccountId());
        System.out.println("Username =                      " + event.getUser());
        System.out.println(event.getUser() + " money account =           " + alice.getMoneyAccount());
        System.out.println("Price of the ordered article =  " + event.getAmount());

        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));
        moneyAccount -= price;
        alice.setMoneyAccount(moneyAccount.toString()+"$");

        System.out.println("Alice new money account =       " + alice.getMoneyAccount() +"\n");
        return alice.getMoneyAccount();
    }

    @EventHandler
    public String on(PaymentRefundedEvent event) {
        Integer moneyAccount = Integer.valueOf(alice.getMoneyAccount().substring(0, alice.getMoneyAccount().length()-1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));

        System.out.println("\nAlice money account =      " + alice.getMoneyAccount());
        System.out.println("Amount to refund =         " + event.getAmount());

        moneyAccount += price;
        alice.setMoneyAccount(moneyAccount.toString()+"$");

        System.out.println("Alice new money account =  " + alice.getMoneyAccount() + "\n");
        return alice.getMoneyAccount();
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
