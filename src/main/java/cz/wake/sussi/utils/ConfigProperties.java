package cz.wake.sussi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ConfigProperties {

    private String botToken;
    private String host, port, dbname, dbuser, dbpassword, minConnections, maxConnections, timeout, ipHubKey, beta, proxycheckKey,
                    dialogFlowApiKey, craftBoxAdminKey;
    private boolean dialogFlowEnabled, metricsEnabled;
    private Long navrhyHlasovaniID, ownerID, secretChannelAtsID, atPokecID, navrhyDiskuzeID, cmGuildID, oznameniID, vytvoritVoiceID;
    private List<String> dialogFlowChannels;

    public ConfigProperties() {
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
            proxycheckKey = properties.getProperty("proxycheckKey");
            beta = properties.getProperty("isBeta");
            metricsEnabled = Boolean.parseBoolean(properties.getProperty("metrics.enabled", "false"));

            dialogFlowApiKey = properties.getProperty("dialogflow-api-key");
            dialogFlowEnabled = Boolean.parseBoolean(properties.getProperty("dialogflow-enabled", "false"));
            dialogFlowChannels = Collections.singletonList(properties.getProperty("dialogflow-channels"));

            cmGuildID = Long.parseLong(properties.getProperty("cm_guild"));
            navrhyDiskuzeID = Long.parseLong(properties.getProperty("navrhy_diskuze"));
            navrhyHlasovaniID = Long.parseLong(properties.getProperty("navrhy_hlasovani"));
            ownerID = Long.parseLong(properties.getProperty("owner"));
            atPokecID = Long.parseLong(properties.getProperty("at_pokec"));
            secretChannelAtsID = Long.parseLong(properties.getProperty("secret_channel_ats"));
            oznameniID = Long.parseLong(properties.getProperty("oznameni"));
            vytvoritVoiceID = Long.parseLong(properties.getProperty("vytvorit_voice"));

            craftBoxAdminKey = properties.getProperty("adminKey");

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
        return Integer.parseInt(minConnections);
    }

    public int getMaxConnections() {
        return Integer.parseInt(maxConnections);
    }

    public int getTimeout() {
        return Integer.parseInt(timeout);
    }

    public String getIpHubKey() {
        return ipHubKey;
    }

    public Boolean isBeta() {
        return Boolean.parseBoolean(beta);
    }

    public String getDialogFlowApiKey() {
        return dialogFlowApiKey;
    }

    public boolean isDialogFlowEnabled() {
        return dialogFlowEnabled;
    }

    public String getProxycheckKey() {
        return proxycheckKey;
    }

    public List<String> getDialogFlowChannels() {
        return dialogFlowChannels;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public Long getCmGuildID() {
        return cmGuildID;
    }

    public Long getNavrhyDiskuzeID() {
        return navrhyDiskuzeID;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public Long getAtPokecID() {
        return atPokecID;
    }

    public Long getSecretChannelAtsID() {
        return secretChannelAtsID;
    }

    public Long getNavrhyHlasovaniID() {
        return navrhyHlasovaniID;
    }

    public Long getOznameniID() {
        return oznameniID;
    }

    public Long getVytvoritVoiceID() { return vytvoritVoiceID; }

    public String getCraftBoxAdminKey() {
        return craftBoxAdminKey;
    }
}
