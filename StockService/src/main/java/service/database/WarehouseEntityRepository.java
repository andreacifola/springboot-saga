package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This interface implements all the methods that we need to query the warehouse db.
 */
public interface WarehouseEntityRepository extends MongoRepository<WarehouseEntity, String> {

    WarehouseEntity findByArticleId(String articleId);

    WarehouseEntity findByArticle(String article);

    WarehouseEntity findByAvailability(Integer availability);

    void deleteByArticle(String article);
}
