package biz.nellemann.ijump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Properties;

public class ConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);

    private final File propertiesFile;
    private Properties configuration;

    private boolean dirty = false;


    public ConfigurationService() {

        String userHome = System.getProperty("user.home");
        File confHome = new File(userHome, ".config");

        File home = new File(confHome.getAbsolutePath());
        if (!home.exists()) {
            log.info("initialize() - Creating config folder: " + home);
            home.mkdirs();
        }
        configuration = new Properties();
        propertiesFile = new File(confHome, "ijump.properties");
        if (propertiesFile.exists()) {
            log.info("initialize() - Loading existing properties file: {}", propertiesFile.toString());
            configuration = loadPropertiesFile(propertiesFile.getAbsolutePath());
        } else {
            log.info("initialize() - Creating new properties file: {}", propertiesFile.toString());
            savePropertiesFile(configuration);
        }

    }


    public boolean contains(String key) {
        return configuration.containsKey(key);
    }


    public String get(String key) {
        return configuration.getProperty(key);
    }


    public String get(String key, String fallback) {
        String value = get(key);
        if(value == null || value.isEmpty()) {
            value = fallback;
        }
        return value;
    }


    public void put(String key, String value) {
        if(value != null && !value.isBlank()) {
            configuration.setProperty(key, value);
            dirty = true;
        }
        save();
    }



    public boolean save() {
        if(dirty) {
            savePropertiesFile(configuration);
            return true;
        }
        return false;
    }


    private void savePropertiesFile(Properties prop) {
        try (OutputStream output = new FileOutputStream(propertiesFile.getAbsolutePath())) {
            prop.store(output, "iJump Configuration");
        } catch (IOException io) {
            log.error("saveProperties() - exception: {}", io.getMessage(), io);
        }
    }


    private Properties loadPropertiesFile(String filename) {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(filename)) {
            prop.load(input);
        } catch (IOException ex) {
            log.error("loadProperties() - exception: {}", ex.getMessage(), ex);
        }
        return prop;
    }


}

