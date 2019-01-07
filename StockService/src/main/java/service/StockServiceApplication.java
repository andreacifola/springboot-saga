package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.database.WarehouseEntity;
import service.database.WarehouseEntityRepository;


@SpringBootApplication
public class StockServiceApplication {

    private static WarehouseEntityRepository warehouseEntityRepository;

    @Autowired
    public StockServiceApplication(WarehouseEntityRepository warehouseEntityRepository) {
        StockServiceApplication.warehouseEntityRepository = warehouseEntityRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);

        warehouseEntityRepository.deleteAll();

        WarehouseEntity wareHouseEntity = new WarehouseEntity("shirt", 23);
        warehouseEntityRepository.save(wareHouseEntity);
    }
}
