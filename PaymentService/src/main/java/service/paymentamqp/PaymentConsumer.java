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

import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;


/**
 * This class receives the events from the SagaOrchestrator and listens to all the messages useful for itself.
 */
@ProcessingGroup("paymentEvents")
@RestController
public class PaymentConsumer {

    private final CommandGateway commandGateway;
    private final CommandBus commandBus;

    private final BankAccountEntityRepository bankAccountEntityRepository;
    private final PaymentEntityRepository paymentEntityRepository;

    @Autowired
    public PaymentConsumer(CommandGateway commandGateway, CommandBus commandBus, BankAccountEntityRepository bankAccountEntityRepository, PaymentEntityRepository paymentEntityRepository) {
        this.commandGateway = commandGateway;
        this.commandBus = commandBus;
        this.bankAccountEntityRepository = bankAccountEntityRepository;
        this.paymentEntityRepository = paymentEntityRepository;
    }

    /**
     * When PaymentService receives the PaymentTriggeredEvent, first of all it search the right user in the
     * bank account db. Then it checks if the bank account has enough money for the order: if yes, it decreases
     * the value of money account for the bank account, prints the transaction, save the payment in the db and
     * sends the DoPaymentCommand to the SagaOrchestrator to continue with the saga. Otherwise  it does not do
     * the transaction and sends to the SagaOrchestrator the AbortPaymentCommand to start the end of the saga.
     *
     * @param event
     * @return
     */
    @EventHandler
    public void on(PaymentTriggeredEvent event) {

        BankAccountEntity user = bankAccountEntityRepository.findByUser(event.getUser());

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
            System.out.println(user.getUser() + " new money account =       " + user.getMoneyAccount() + "\n");

            bankAccountEntityRepository.save(user);
            paymentEntityRepository.save(new PaymentEntity(event.getPaymentId(), user.getAccountId(), user.getUser(), event.getAmount()));

            commandBus.dispatch(asCommandMessage(new TriggerPaymentCommand(event.getPaymentId(), user.getAccountId(), event.getUser(), event.getAmount())));
            commandGateway.send(new DoPaymentCommand(event.getPaymentId(), user.getAccountId(), event.getUser(), event.getAmount()));
        } else {
            System.out.println("\nYou don't have enough money in your bank account!\n");
            commandBus.dispatch(asCommandMessage(new TriggerPaymentCommand(event.getPaymentId(), user.getAccountId(), event.getUser(), event.getAmount())));
            commandGateway.send(new AbortPaymentCommand(event.getPaymentId(), user.getAccountId(), event.getUser(), event.getAmount()));
        }
    }

    /**
     * When PaymentService receives the PaymentTriggeredEvent, it means that the StockService has aborted its
     * transaction. So we need to refund the bank user in the bank account. Then we send the
     * TriggerEndSagaPaymentCommand to advise the SagaOrchestrator that we are done with the compensating action.
     * @param event
     * @return
     */
    @EventHandler
    public void on(PaymentRefundedEvent event) {

        BankAccountEntity user = bankAccountEntityRepository.findByUser(event.getUser());

        Integer moneyAccount = Integer.valueOf(user.getMoneyAccount().substring(0, user.getMoneyAccount().length() - 1));
        Integer price = Integer.valueOf(event.getAmount().substring(0, event.getAmount().length()-1));

        System.out.println("\n" + user.getUser() + " money account =      " + user.getMoneyAccount());
        System.out.println("Amount to refund =         " + event.getAmount());

        moneyAccount += price;
        user.setMoneyAccount(moneyAccount.toString() + "$");

        System.out.println(user.getUser() + "new money account =  " + user.getMoneyAccount() + "\n");

        bankAccountEntityRepository.save(user);
        PaymentEntity paymentEntity = paymentEntityRepository.findByUser(event.getUser());
        paymentEntityRepository.delete(paymentEntity);

        commandGateway.send(new TriggerEndSagaPaymentCommand(event.getPaymentId(), user.getAccountId(), event.getUser(), event.getAmount()));
    }

    /**
     * This is the classic method used by Axon to listen messages
     * from the specified Queue, in this case the Payment queue.
     * @param serializer
     * @return
     */
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
