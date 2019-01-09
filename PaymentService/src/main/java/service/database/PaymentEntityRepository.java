package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentEntityRepository extends MongoRepository<PaymentEntity, String> {

    PaymentEntity findByPaymentId(String paymentId);

    PaymentEntity findByAccountId(String accountId);

    PaymentEntity findByUser(String user);

    PaymentEntity findByAmount(String amount);
}
