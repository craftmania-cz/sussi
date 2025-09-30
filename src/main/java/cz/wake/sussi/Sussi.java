package cz.wake.sussi;

import cz.wake.sussi.commands.SlashCommandHandler;
import cz.wake.sussi.commands.console.ConsoleCommandManager;
import cz.wake.sussi.listeners.*;
import cz.wake.sussi.objects.NotificationCacheObject;
import cz.wake.sussi.objects.VIPManager;
import cz.wake.sussi.runnable.*;
import cz.wake.sussi.objects.notes.NoteManager;
import cz.wake.sussi.sql.SQLManager;
import cz.wake.sussi.utils.ConfigProperties;
import cz.wake.sussi.utils.SussiLogger;
import dev.mayuna.mayusjdautils.interactive.InteractiveListener;
import lombok.Getter;
import lombok.Setter;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.dv8tion.jda.internal.utils.JDALogger.getLog;

public class Sussi {

    public static final @Deprecated String PREFIX = ",";
    public static @Getter long startUp;
    public static @Getter NoteManager noteManager;
    public static @Getter ATSResetTask atsManager;
    public static @Getter VoteResetTask voteManager;
    public static @Getter VIPManager vipManager;
    public static @Getter ConfigProperties config;

    private static @Getter Sussi instance;
    private static @Getter JDA jda;
    private static @Getter @Setter boolean isBeta = true;
    private @Getter MainListener events;
    private @Getter SQLManager sql;
    private static @Getter SlashCommandHandler slashCommandHandler;
    private static final @Getter Map<String, Logger> loggerMap;
    private static final @Getter Logger logger;
    private final @Getter HashMap<String, NotificationCacheObject> notificationCache = new HashMap<>();

    static {
        new File("logs/latest.log").renameTo(new File("logs/log-" + getCurrentTimeStamp() + ".log"));
        loggerMap = new ConcurrentHashMap<>();
        logger = getLog(Sussi.class);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        // Startup info
        SussiLogger.infoMessage("Now will Sussi wake up!");

        SussiLogger.infoMessage("Initializing console commands...");
        ConsoleCommandManager.init();

        // Config
        SussiLogger.infoMessage("Loading config...");
        config = new ConfigProperties();
        isBeta = config.isBeta();

        startUp = System.currentTimeMillis();

        // Connecting to Discord API
        SussiLogger.infoMessage("Connecting to Discord API...");
        jda = JDABuilder.createDefault(config.getBotToken())
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new MainListener(), new CraftManiaArchiveListener())
                .addEventListeners(new BoosterListener())
                .addEventListeners(new ChangelogReactionsListener())
                .addEventListeners(new GuildStatisticsListener())
                .addEventListeners(new AtReputationChannelListener())
                .addEventListeners(new SlashCommandListener())
                .addEventListeners(new ButtonClickListener())
                .addEventListeners(new InteractiveListener())
                .addEventListeners(new SelectionMenuListener())
                .addEventListeners(new ModalListener())
                .setActivity(Activity.playing("Načítám se..."))
                .build().awaitReady();

        slashCommandHandler = new SlashCommandHandler();
        slashCommandHandler.createAndUpdateCommands();

        // MySQL
        SussiLogger.infoMessage("Connection to MySQL...");

        try {
            (instance = new Sussi()).initDatabase();
            SussiLogger.greatMessage("Sussi is successful connected to MySQL.");
        } catch (Exception e) {
            SussiLogger.errorMessage("During connection to MySQL, error has occurred:");
            e.printStackTrace();
            System.exit(-1);
        }
        atsManager = new ATSResetTask();

        if (!isBeta) {
            SussiLogger.infoMessage("Sussi will run as production bot.");

            // Inicializace základních managerů
            noteManager = new NoteManager();
            voteManager = new VoteResetTask();
            vipManager = new VIPManager();

            // Tasks
            try {
                scheduleTasks();
            } catch (Exception e) {
                e.printStackTrace();
            }

            jda.getPresence().setActivity(Activity.competing("Minecraft"));
        } else {
            SussiLogger.infoMessage("Sussi is set to test mode, some functions may not work properly.");
            jda.getPresence().setActivity(Activity.of(Activity.ActivityType.PLAYING, "Testovací režim."));
            jda.getPresence().setStatus(OnlineStatus.IDLE);
        }
    }

    private void initDatabase() {
        sql = new SQLManager(this);
    }

    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    private static void scheduleTasks() throws Exception {
        SussiLogger.infoMessage("Start of scheduling tasks");
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        // ATS Reset
        /* try {
            JobDetail job = JobBuilder.newJob(ATSResetTask.class)
                    .withIdentity("atsEvaluation")
                    .build();
            CronTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("atsEvaluation")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 8 5 1/1 ? *")) // 5th day of every month on 8am
                    .build();
            scheduler.scheduleJob(job, ITrigger);
        } catch (Exception e) {
            e.printStackTrace();
        } */

        // CraftMania Statistics reset
        /*
        try {
            JobDetail job = JobBuilder.newJob(VoteResetTask.class)
                    .withIdentity("monthVotesEvaluation")
                    .build();
            CronTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("monthVotesEvaluation")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 1 1/1 ? *")) // every 1st of month on 1am
                    .build();
            scheduler.scheduleJob(job, ITrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JobDetail job = JobBuilder.newJob(DailyBonusResetJob.class)
                    .withIdentity("dailyBonusReset")
                    .build();
            CronTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("dailyBonusReset")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ? *")) // everyday on 1 am
                    .build();
            scheduler.scheduleJob(job, ITrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JobDetail job = JobBuilder.newJob(WeekVotesJob.class)
                    .withIdentity("weekVotesReset")
                    .build();
            CronTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("weekVotesReset")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 ? * MON"))
                    .build();
            scheduler.scheduleJob(job, ITrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JobDetail job = JobBuilder.newJob(StatusChangerTask.class)
                    .withIdentity("statusChangeTask")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("statusChangeTask").startNow().withSchedule(SimpleScheduleBuilder.repeatMinutelyForever()).build();
            scheduler.scheduleJob(job, ITrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            JobDetail job = JobBuilder.newJob(EmptyVoiceCheckTask.class)
                    .withIdentity("emptyVoiceCheck")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("emptyVoiceCheck").startNow().withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(3)).build();
            scheduler.scheduleJob(job, ITrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Synchronization with VIP on Minecraft server
        try {
            JobDetail job = JobBuilder.newJob(VIPCheckJob.class)
                    .withIdentity("vipCheck")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("vipCheck").withSchedule(SimpleScheduleBuilder.repeatHourlyForever(2)).build(); // Every 2 hours
            scheduler.scheduleJob(job, ITrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JobDetail job = JobBuilder.newJob(BoosterCheckerTask.class)
                    .withIdentity("boosterCheck")
                    .build();
            SimpleTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("boosterCheck").withSchedule(SimpleScheduleBuilder.repeatHourlyForever(1)).build(); // Every 1 hour
            scheduler.start();
            scheduler.scheduleJob(job, ITrigger);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            JobDetail job = JobBuilder.newJob(DailyPollTask.class)
                    .withIdentity("dailyPoll")
                    .build();
            CronTrigger ITrigger = TriggerBuilder.newTrigger()
                    .forJob("dailyPoll")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 6 * * ? *")) // every day at 6:00 AM
                    .build();
            scheduler.scheduleJob(job, ITrigger);
            SussiLogger.infoMessage("Daily poll task scheduled.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SussiLogger.greatMessage("All tasks were successfully scheduled.");
    }
}
