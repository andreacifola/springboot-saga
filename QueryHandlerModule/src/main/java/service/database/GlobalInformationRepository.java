package service.database;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This interface implements all the methods that we need to query the global information db.
 */
public interface GlobalInformationRepository extends MongoRepository<GlobalInformation, String> {

    GlobalInformation findByOrderId(String orderId);

    GlobalInformation findByUser(String user);

    GlobalInformation findByArticle(String article);

    GlobalInformation findByQuantity(Integer quantity);

    GlobalInformation findByAvailability(Integer availability);

    GlobalInformation findByPrice(String price);
}
