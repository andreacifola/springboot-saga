package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.database.WarehouseEntity;
import service.database.WarehouseEntityRepository;

import java.util.UUID;


@SpringBootApplication
public class StockServiceApplication {

    private static WarehouseEntityRepository warehouseEntityRepository;

    @Autowired
    public StockServiceApplication(WarehouseEntityRepository warehouseEntityRepository) {
        StockServiceApplication.warehouseEntityRepository = warehouseEntityRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);

        String articleId = UUID.randomUUID().toString();
        warehouseEntityRepository.deleteAll();

        WarehouseEntity wareHouseEntity = new WarehouseEntity(articleId, "shirt", 23);
        warehouseEntityRepository.save(wareHouseEntity);
    }
}
