package cz.wake.sussi;

import cz.wake.sussi.commands.CommandHandler;
import cz.wake.sussi.listeners.MainListener;
import cz.wake.sussi.utils.LoadingProperties;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Sussi {

    private static Sussi instance;
    private MainListener events;
    private static JDA jda;
    private CommandHandler ch = new CommandHandler();
    public static final String PREFIX = ",";
    public static long startUp;

    public static void main(String[] args) throws LoginException, RateLimitedException, InterruptedException, IOException {

        LoadingProperties config = new LoadingProperties();

        EventWaiter waiter = new EventWaiter();

        startUp = System.currentTimeMillis();

        jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getBotToken())
                .addEventListener(new MainListener(waiter))
                .addEventListener(waiter)
                .buildBlocking();

        (instance = new Sussi()).init();

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


}
