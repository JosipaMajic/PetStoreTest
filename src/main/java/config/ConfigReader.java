package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();

    static {
        try (FileInputStream fileInputStream = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.err.println("Failed to load config.properties: " + e.getMessage());
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("base_url");
    }

    public static String getApiKey() {
        return properties.getProperty("api_key");
    }

    public static String getContentType() {
        return properties.getProperty("content_type");
    }
}
