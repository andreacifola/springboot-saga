package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.database.StockEntityRepository;
import service.database.WarehouseEntity;
import service.database.WarehouseEntityRepository;

import java.util.UUID;


@SpringBootApplication
public class StockServiceApplication {

    private static WarehouseEntityRepository warehouseEntityRepository;
    private static StockEntityRepository stockEntityRepository;

    @Autowired
    public StockServiceApplication(WarehouseEntityRepository warehouseEntityRepository, StockEntityRepository stockEntityRepository) {
        StockServiceApplication.warehouseEntityRepository = warehouseEntityRepository;
        StockServiceApplication.stockEntityRepository = stockEntityRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);

        String articleId = UUID.randomUUID().toString();
        //warehouseEntityRepository.deleteAll();
        WarehouseEntity wareHouseEntity = new WarehouseEntity(articleId, "shirt", 23);
        if (warehouseEntityRepository.findByArticle("shirt") == null) {
            warehouseEntityRepository.save(wareHouseEntity);
        } else {
            warehouseEntityRepository.deleteByArticle("shirt");
            warehouseEntityRepository.save(wareHouseEntity);
        }

        //TODO eliminare quando è finito
        stockEntityRepository.deleteAll();

    }
}
