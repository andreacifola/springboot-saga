package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.database.BankAccountEntity;
import service.database.BankAccountEntityRepository;
import service.database.PaymentEntityRepository;

import java.util.UUID;


@SpringBootApplication
public class PaymentServiceApplication {

    private static BankAccountEntityRepository bankAccountEntityRepository;
    private static PaymentEntityRepository paymentEntityRepository;

    @Autowired
    public PaymentServiceApplication(BankAccountEntityRepository bankAccountEntityRepository, PaymentEntityRepository paymentEntityRepository) {
        PaymentServiceApplication.bankAccountEntityRepository = bankAccountEntityRepository;
        PaymentServiceApplication.paymentEntityRepository = paymentEntityRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);

        String accountId = UUID.randomUUID().toString();
        //bankAccountEntityRepository.deleteAll();
        BankAccountEntity user = bankAccountEntityRepository.findByUser("Alice");
        if (user != null)
            bankAccountEntityRepository.delete(user);

        //TODO eliminare quando è finito
        paymentEntityRepository.deleteAll();

    }
}
