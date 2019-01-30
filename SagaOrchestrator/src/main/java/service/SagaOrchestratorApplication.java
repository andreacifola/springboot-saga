package service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.logging.LogFile;

import java.util.UUID;
import java.util.logging.Logger;


@SpringBootApplication
public class SagaOrchestratorApplication {

    public static String sagaId;

    public static Logger logger = LogFile.writeLogFile();

    public static void main(String[] args) {
        SpringApplication.run(SagaOrchestratorApplication.class, args);

        sagaId = UUID.randomUUID().toString();
        System.out.println("\nSaga:    " + sagaId);
    }
}
