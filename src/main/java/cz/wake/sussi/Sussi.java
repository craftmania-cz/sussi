package cz.wake.sussi;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandHandler;
import cz.wake.sussi.commands.SlashCommandHandler;
import cz.wake.sussi.listeners.*;
import cz.wake.sussi.objects.VIPManager;
import cz.wake.sussi.objects.jobs.VIPCheckJob;
import cz.wake.sussi.objects.jobs.WeekVotesJob;
import cz.wake.sussi.runnable.*;
import cz.wake.sussi.objects.notes.NoteManager;
import cz.wake.sussi.sql.SQLManager;
import cz.wake.sussi.utils.ConfigProperties;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.dv8tion.jda.internal.utils.JDALogger.getLog;

public class Sussi {

    private static Sussi instance;
    private MainListener events;
    private static JDA jda;
    private SQLManager sql;
    private CommandHandler ch = new CommandHandler();
    private static SlashCommandHandler slashCommandHandler;
    public static final String PREFIX = ",";
    public static long startUp;
    public static String API_URL = "";
    private static String ipHubKey = "";
    private static boolean isBeta = true;
    private static final Map<String, Logger> LOGGERS;
    public static final Logger LOGGER;
    public static NoteManager noteManager;
    public static ATSResetTask atsManager;
    public static VoteResetTask voteManager;
    public static VIPManager vipManager;
    public static ConfigProperties config;

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
        config = new ConfigProperties();
        ipHubKey = config.getIpHubKey();
        isBeta = config.isBeta();

        EventWaiter waiter = new EventWaiter();

        startUp = System.currentTimeMillis();

        // Connecting to Discord API
        SussiLogger.infoMessage("Connecting to Discord API...");
        jda = JDABuilder.createDefault(config.getBotToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new MainListener(waiter), new CraftManiaArchiveListener())
                .addEventListeners(waiter)
                .addEventListeners(new BoosterListener())
                .addEventListeners(new ChangelogReactionsListener())
                .addEventListeners(new GuildStatisticsListener())
                .addEventListeners(new SlashCommandListener())
                .addEventListeners(new ButtonClickListener())
                .setActivity(Activity.playing("Načítám se..."))
                .build().awaitReady();

        // Register commands
        (instance = new Sussi()).init();

        slashCommandHandler = new SlashCommandHandler();
        slashCommandHandler.createAndUpdateCommands();

        // MySQL
        SussiLogger.infoMessage("Connection to MySQL...");

        try {
            (instance = new Sussi()).initDatabase();
            SussiLogger.greatMessage("Sussi is successful connected to MySQL.");
            SussiLogger.infoMessage("Sussi will run as PRODUCTION bot.");
        } catch (Exception e) {
            SussiLogger.dangerMessage("During connection to MySQL, error has occurred:");
            e.printStackTrace();
            System.exit(-1);
        }

        if (!isBeta) {

            // Inicializace základních managerů
            noteManager = new NoteManager();
            atsManager = new ATSResetTask();
            voteManager = new VoteResetTask();
            vipManager = new VIPManager();

            // Tasks
            scheduleTasks();
        } else {
            jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Testovací režim."));
            jda.getPresence().setStatus(OnlineStatus.IDLE);
        }
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

    public static SlashCommandHandler getSlashCommandHandler() {
        return slashCommandHandler;
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

    public static NoteManager getNoteManager() {
        return noteManager;
    }

    public static ATSResetTask getATSManager() {
        return atsManager;
    }

    public static VoteResetTask getVoteManager() {
        return voteManager;
    }

    public static VIPManager getVIPManager() {
        return vipManager;
    }

    public static ConfigProperties getConfig() {
        return config;
    }

    private static void scheduleTasks() {

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(ATSResetTask.class)
                    .withIdentity("atsEvaluation")
                    .build();
            CronTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("atsEvaluation")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 8 5 1/1 ? *")) // 5th day of every month on 8am
                    .build();
            scheduler.start();
            scheduler.scheduleJob(job, ITrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(VoteResetTask.class)
                    .withIdentity("monthVotesEvaluation")
                    .build();
            CronTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("monthVotesEvaluation")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 1 1/1 ? *")) // every 1st of month on 1am
                    .build();
            scheduler.start();
            scheduler.scheduleJob(job, ITrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(WeekVotesJob.class)
                    .withIdentity("weekVotesReset")
                    .build();
            CronTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("weekVotesReset")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 ? * MON"))
                    .build();
            scheduler.start();
            scheduler.scheduleJob(job, ITrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(StatusChangerTask.class)
                    .withIdentity("statusChangeTask")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("statusChangeTask").startNow().withSchedule(SimpleScheduleBuilder.repeatMinutelyForever()).build();
            scheduler.start();
            scheduler.scheduleJob(job, ITrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(EmptyVoiceCheckTask.class)
                    .withIdentity("emptyVoiceCheck")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("emptyVoiceCheck").startNow().withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(3)).build();
            scheduler.start();
            scheduler.scheduleJob(job, ITrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

      try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(VIPCheckJob.class)
                    .withIdentity("vipCheck")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("vipCheck").withSchedule(SimpleScheduleBuilder.repeatHourlyForever(3)).build(); // Every 3 hours
            scheduler.start();
            scheduler.scheduleJob(job, ITrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(TextActivityTask.class)
                    .withIdentity("textActivityCheck")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("textActivityCheck").withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(1)).build(); // Every 1 minute
            scheduler.scheduleJob(job, ITrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(BoosterCheckerTask.class)
                    .withIdentity("boosterCheck")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("boosterCheck").withSchedule(SimpleScheduleBuilder.repeatHourlyForever(1)).build(); // Every 1 hour
            scheduler.start();
            scheduler.scheduleJob(job, ITrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
