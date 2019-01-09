package service.paymentamqp;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import service.coreapi.*;
import service.database.BankAccountEntity;
import service.database.BankAccountEntityRepository;
import service.database.PaymentEntity;
import service.database.PaymentEntityRepository;

import java.util.UUID;

import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;


@ProcessingGroup("paymentEvents")
@RestController
public class PaymentConsumer {

    private final CommandGateway commandGateway;
    private final CommandBus commandBus;

    @Autowired
    private BankAccountEntityRepository bankAccountEntityRepository;
    @Autowired
    private PaymentEntityRepository paymentEntityRepository;

    private String paymentId = UUID.randomUUID().toString();

    public PaymentConsumer(CommandGateway commandGateway, CommandBus commandBus) {
        this.commandGateway = commandGateway;
        this.commandBus = commandBus;
    }

    @EventHandler
    public void on(PaymentTriggeredEvent event) {
        //todo migliorare questa cosa, mettendo un paymentId al posto dell'accountId come aggregateidentifier altrimenti va in loop mongodb
        paymentId = UUID.randomUUID().toString();

        //TODO eliminare quando è finito
        paymentEntityRepository.deleteAll();

        BankAccountEntity user = bankAccountEntityRepository.findByUser("Alice");

        Integer moneyAccount = Integer.valueOf(user.getMoneyAccount().substring(0, user.getMoneyAccount().length() - 1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length() - 1));

        if (moneyAccount >= price) {

            System.out.println("\nAccount Id =                    " + user.getAccountId());
            System.out.println("Payment Id =                    " + event.getPaymentId());
            System.out.println("Username =                      " + event.getUser());
            System.out.println(event.getUser() + " money account =           " + user.getMoneyAccount());
            System.out.println("Price of the ordered article =  " + event.getAmount());

            moneyAccount -= price;
            user.setMoneyAccount(moneyAccount.toString() + "$");
            System.out.println("Alice new money account =       " + user.getMoneyAccount() + "\n");

            bankAccountEntityRepository.save(user);
            paymentEntityRepository.save(new PaymentEntity(paymentId, user.getUser(), event.getAmount()));

            commandBus.dispatch(asCommandMessage(new TriggerPaymentCommand(event.getPaymentId(), event.getUser(), event.getAmount())));
            commandGateway.send(new DoPaymentCommand(event.getPaymentId(), event.getUser(), event.getAmount()));
        } else {
            System.out.println("\nYou don't have enough money in your bank account!\n");
            commandBus.dispatch(asCommandMessage(new TriggerPaymentCommand(event.getPaymentId(), event.getUser(), event.getAmount())));
            commandGateway.send(new AbortPaymentCommand(event.getPaymentId(), event.getUser(), event.getAmount()));
        }
    }

    @EventHandler
    public void on(PaymentRefundedEvent event) {
        //todo migliorare questa cosa, mettendo un paymentId al posto dell'accountId come aggregateidentifier altrimenti va in loop mongodb
        paymentId = UUID.randomUUID().toString();

        BankAccountEntity user = bankAccountEntityRepository.findByUser("Alice");

        Integer moneyAccount = Integer.valueOf(user.getMoneyAccount().substring(0, user.getMoneyAccount().length() - 1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));

        System.out.println("\nAlice money account =      " + user.getMoneyAccount());
        System.out.println("Amount to refund =         " + event.getAmount());

        moneyAccount += price;
        user.setMoneyAccount(moneyAccount.toString() + "$");

        System.out.println("Alice new money account =  " + user.getMoneyAccount() + "\n");

        bankAccountEntityRepository.save(user);
        //TODO controllare dopo aver aggiustato la cosa del paymentId
        PaymentEntity paymentEntity = paymentEntityRepository.findByPaymentId(event.getPaymentId());
        paymentEntityRepository.delete(paymentEntity);

        commandGateway.send(new TriggerEndSagaPaymentCommand(event.getPaymentId(), event.getUser(), event.getAmount()));
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
