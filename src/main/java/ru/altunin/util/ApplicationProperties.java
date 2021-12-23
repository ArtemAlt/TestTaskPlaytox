package ru.altunin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class ApplicationProperties {
    private static final String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
    private static final String appConfigPath = rootPath + "application.properties";
    private static final Logger logger = LoggerFactory.getLogger(ApplicationProperties.class);

    public static Properties getAppProps() {
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            logger.error("Не найдет файл с настройками приложения");
        }
        return appProps;
    }

}
