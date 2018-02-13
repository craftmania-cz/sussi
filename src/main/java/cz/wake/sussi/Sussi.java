package cz.wake.sussi;

import cz.wake.sussi.commands.CommandHandler;
import cz.wake.sussi.listeners.MainListener;
import cz.wake.sussi.runnable.StatusChanger;
import cz.wake.sussi.sql.SQLManager;
import cz.wake.sussi.utils.LoadingProperties;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Timer;

public class Sussi {

    private static Sussi instance;
    private MainListener events;
    private static JDA jda;
    private SQLManager sql;
    private CommandHandler ch = new CommandHandler();
    public static final String PREFIX = ",";
    public static long startUp;
    private static String ipHubKey = "";

    public static void main(String[] args) throws LoginException, InterruptedException, IOException, RateLimitedException {

        LoadingProperties config = new LoadingProperties();
        ipHubKey = config.getIpHubKey();

        EventWaiter waiter = new EventWaiter();

        startUp = System.currentTimeMillis();

        jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getBotToken())
                .addEventListener(new MainListener(waiter))
                .addEventListener(waiter)
                .setGame(Game.of(Game.GameType.DEFAULT, "Načítání..."))
                .buildBlocking();

        (instance = new Sussi()).init();
        (instance = new Sussi()).initDatabase();

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

    public static String getIpHubKey() {
        return ipHubKey;
    }
}
