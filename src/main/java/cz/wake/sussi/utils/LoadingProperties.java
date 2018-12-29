package cz.wake.sussi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class LoadingProperties {

    private String botToken;
    private String host, port, dbname, dbuser, dbpassword, minConnections, maxConnections, timeout, ipHubKey, beta;

    public LoadingProperties() {
        try {
            File configFile = new File("config.yml");

            FileInputStream fileInput = new FileInputStream(configFile);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            botToken = properties.getProperty("token");
            host = properties.getProperty("hostname");
            port = properties.getProperty("port");
            dbname = properties.getProperty("database");
            dbuser = properties.getProperty("username");
            dbpassword = properties.getProperty("password");
            minConnections = properties.getProperty("minimumConnections");
            maxConnections = properties.getProperty("maximumConnections");
            timeout = properties.getProperty("timeout");
            ipHubKey = properties.getProperty("ipHubKey");
            beta = properties.getProperty("isBeta");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBotToken() {
        return botToken;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDbname() {
        return dbname;
    }

    public String getDbuser() {
        return dbuser;
    }

    public String getDbpassword() {
        return dbpassword;
    }

    public int getMinConnections() {
        return Integer.valueOf(minConnections);
    }

    public int getMaxConnections() {
        return Integer.valueOf(maxConnections);
    }

    public int getTimeout() {
        return Integer.valueOf(timeout);
    }

    public String getIpHubKey() {
        return ipHubKey;
    }

    public Boolean isBeta() {
        return Boolean.parseBoolean(beta);
    }
}
