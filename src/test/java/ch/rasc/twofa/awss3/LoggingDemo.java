package ch.rasc.twofa.awss3;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LoggingDemo {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("LoggingDemo");

        try {
            FileHandler fileHandler = new FileHandler("C:/Users/Ufinity/test.txt");
            logger.addHandler(fileHandler);
            logger.info("info");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.severe("severe");
        logger.warning("warning");
        logger.info("info");
        logger.config("config");
    }
}
