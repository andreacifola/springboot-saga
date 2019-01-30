package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This interface implements all the methods that we need to query the bank account db.
 */
public interface BankAccountEntityRepository extends MongoRepository<BankAccountEntity, String> {

    BankAccountEntity findByAccountId(String accountId);

    BankAccountEntity findByUser(String user);

    BankAccountEntity findByMoneyAccount(String moneyAccount);

    void deleteByUser(String user);
}
