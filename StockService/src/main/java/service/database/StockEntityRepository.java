package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This interface implements all the methods that we need to query the stock db.
 */
public interface StockEntityRepository extends MongoRepository<StockEntity, String> {

    StockEntity findByArticleId(String articleId);

    StockEntity findByArticle(String article);

    StockEntity findByQuantity(Integer quantity);
}
