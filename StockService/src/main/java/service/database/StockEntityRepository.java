package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockEntityRepository extends MongoRepository<StockEntity, String> {

    StockEntity findByArticleId(String articleId);

    StockEntity findByArticle(String article);

    StockEntity findByQuantity(Integer quantity);
}
