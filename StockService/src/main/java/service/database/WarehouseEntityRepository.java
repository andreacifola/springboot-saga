package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WarehouseEntityRepository extends MongoRepository<WarehouseEntity, String> {

    WarehouseEntity findByArticleId(String articleId);

    WarehouseEntity findByArticle(String article);

    WarehouseEntity findByAvailability(Integer availability);
}
