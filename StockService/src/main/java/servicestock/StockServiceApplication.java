package servicestock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EntityScan(basePackages = {"the package on which it depends"}) //Add this only if it realy depends on the SagaOrchestrator module
//@ComponentScan(basePackages = {"the package on which it depends"}) //Add this only if it realy depends on the SagaOrchestrator module
public class StockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class);
    }
}
