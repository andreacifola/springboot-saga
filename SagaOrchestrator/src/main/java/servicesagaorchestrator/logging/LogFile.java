package servicesagaorchestrator.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogFile {

    public static Logger writeLogFile() {
        Logger logger = Logger.getLogger("Log");
        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter
            // The second parameter allow us to append on existing file the messages
            fh = new FileHandler("/Users/andreacifola/Desktop/Progetto Tesi/springboot-saga/SagaOrchestrator/LogFile.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        return logger;
    }
}
