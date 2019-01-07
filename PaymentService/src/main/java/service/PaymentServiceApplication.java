package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.database.BankAccountEntity;
import service.database.BankAccountEntityRepository;

import java.util.UUID;


@SpringBootApplication
public class PaymentServiceApplication {

    private static BankAccountEntityRepository bankAccountEntityRepository;
    private static String accountId = UUID.randomUUID().toString();

    @Autowired
    public PaymentServiceApplication(BankAccountEntityRepository bankAccountEntityRepository) {
        PaymentServiceApplication.bankAccountEntityRepository = bankAccountEntityRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);

        bankAccountEntityRepository.deleteAll();

        BankAccountEntity alice = new BankAccountEntity(accountId, "Alice", "350$");
        bankAccountEntityRepository.save(alice);
    }
}
