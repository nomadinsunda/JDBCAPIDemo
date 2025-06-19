package com.intheeast.jdbcapi.basic.tutorial.jdbc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbConfigLoader {

    private final Properties properties = new Properties();

    public DbConfigLoader(String propertiesFilePath) throws IOException {
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        }
    }

    public String getUrl() {
        return properties.getProperty("DB.URL");
    }

    public String getUser() {
        return properties.getProperty("DB.USER");
    }

    public String getPassword() {
        return properties.getProperty("DB.PASSWORD");
    }

    public String getDriver() {
        return properties.getProperty("DB.DRIVER");
    }

    public String getVendor() {
        return properties.getProperty("DB.VENDOR");
    }

    public String getDelimiter() {
        return properties.getProperty("DB.DELIMITER", ";");
    }

    public Properties getAllProperties() {
        return properties;
    }
}
