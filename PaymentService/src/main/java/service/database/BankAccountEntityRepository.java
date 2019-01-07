package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BankAccountEntityRepository extends MongoRepository<BankAccountEntity, String> {

    BankAccountEntity findByAccountId(String accountId);

    BankAccountEntity findByUser(String user);

    BankAccountEntity findByMoneyAccount(String moneyAccount);
}
