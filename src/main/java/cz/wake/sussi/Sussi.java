package cz.wake.sussi;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandHandler;
import cz.wake.sussi.listeners.DialogFlowListener;
import cz.wake.sussi.listeners.MainListener;
import cz.wake.sussi.metrics.Metrics;
import cz.wake.sussi.runnable.StatusChanger;
import cz.wake.sussi.sql.SQLManager;
import cz.wake.sussi.utils.LoadingProperties;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import static net.dv8tion.jda.core.utils.JDALogger.getLog;

public class Sussi {

    private static Sussi instance;
    private MainListener events;
    private static JDA jda;
    private SQLManager sql;
    private CommandHandler ch = new CommandHandler();
    public static final String PREFIX = ",";
    public static long startUp;
    public static String API_URL = "";
    private static String ipHubKey = "";
    private static boolean isBeta = true;
    private static final Map<String, Logger> LOGGERS;
    public static final Logger LOGGER;

    static {
        new File("logs/latest.log").renameTo(new File("logs/log-" + getCurrentTimeStamp() + ".log"));
        LOGGERS = new ConcurrentHashMap<>();
        LOGGER = getLog(Sussi.class);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        // Startup info
        SussiLogger.infoMessage("Now will Sussi wake up!");

        // Config
        SussiLogger.infoMessage("Loading config...");
        LoadingProperties config = new LoadingProperties();
        ipHubKey = config.getIpHubKey();
        isBeta = config.isBeta();

        EventWaiter waiter = new EventWaiter();

        startUp = System.currentTimeMillis();

        // Dialogflow
        AIConfiguration aiConfig = new AIConfiguration(config.getDialogFlowApiKey());
        AIDataService aiDataService = new AIDataService(aiConfig);

        // Connecting to Discord API
        SussiLogger.infoMessage("Connecting to Discord API...");
        jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getBotToken())
                .addEventListener(new MainListener(waiter))
                .addEventListener(waiter)
                .addEventListener(new DialogFlowListener(aiDataService))
                .setGame(Game.of(Game.GameType.DEFAULT, "Načítání..."))
                .build().awaitReady();

        // Register commands
        (instance = new Sussi()).init();

        // Metrics
        //Metrics.setup();

        // isBeta and MySQL
        if (!isBeta) {
            SussiLogger.infoMessage("Connection to MySQL...");

            try {
                (instance = new Sussi()).initDatabase();
                SussiLogger.greatMessage("Sussi is successful connected to MySQL.");
                SussiLogger.infoMessage("Sussi will run as PRODUCTION bot.");
                isBeta = false;
            } catch (Exception e) {
                SussiLogger.dangerMessage("During connection to MySQL, error has occurred:");
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            SussiLogger.warnMessage("Database is off, Sussi will not load and save anything!");
            SussiLogger.warnMessage("Sussi is running as BETA bot! Some functions will not work!");
        }

        // StatusChanger
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new StatusChanger(), 10, 60000);

    }

    public static Sussi getInstance() {
        return instance;
    }

    public MainListener getEvents() {
        return events;
    }

    public static JDA getJda() {
        return jda;
    }

    public CommandHandler getCommandHandler() {
        return ch;
    }

    private void init() {
        ch.register();
    }

    public static long getStartUp() {
        return startUp;
    }

    private void initDatabase() {
        sql = new SQLManager(this);
    }

    public SQLManager getSql() {
        return sql;
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public static String getApiUrl(){
        return API_URL;
    }

    public static String getIpHubKey() {
        return ipHubKey;
    }
}
