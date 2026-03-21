package com.nexusqa.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static Properties properties;
    private static ConfigManager instance;
    private static final String CONFIG_PATH = "src/main/resources/config.properties";

    private ConfigManager() {
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            System.out.println("✅ Config loaded successfully!");
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to load config.properties: " + e.getMessage());
        }
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) throw new RuntimeException("❌ Key not found in config: " + key);
        return value.trim();
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    // Shortcuts
    public String getAppUrl()      { return get("app.url"); }
    public String getBrowser()     { return get("browser"); }
    public String getApiBaseUrl()  { return get("api.base.url"); }
    public String getOllamaUrl()   { return get("ollama.url"); }
    public String getOllamaModel() { return get("ollama.model"); }
    public boolean isGridEnabled() { return getBoolean("grid.enabled"); }
    public boolean isAiEnabled()   { return getBoolean("ai.enabled"); }
}