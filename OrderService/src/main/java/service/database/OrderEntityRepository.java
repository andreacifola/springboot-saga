package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderEntityRepository extends MongoRepository<OrderEntity, String> {

    OrderEntity findByOrderId(String orderId);

    OrderEntity findByUser(String user);

    OrderEntity findByArticle(String article);

    OrderEntity findByQuantity(Integer quantity);

    OrderEntity findByPrice(String price);
}
