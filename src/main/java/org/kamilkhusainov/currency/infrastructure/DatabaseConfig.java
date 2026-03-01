package org.kamilkhusainov.currency.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DatabaseConfig {
    private DatabaseConfig(){}

    public static String getUrl(){
        InputStream stream = CurrencyDatabaseInitializer.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("database.url");
}
}
