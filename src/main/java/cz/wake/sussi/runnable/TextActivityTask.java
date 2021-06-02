package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.listeners.GuildStatisticsListener;
import cz.wake.sussi.utils.SussiLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;

public class TextActivityTask implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        HashMap<String, Long> messageLogCopy = (HashMap<String, Long>) GuildStatisticsListener.messageLog.clone();

        //SussiLogger.infoMessage("Updating text activity for " + messageLogCopy.size() + " users...");

        GuildStatisticsListener.messageLog.clear();
        messageLogCopy.forEach((userId, messagesCount) -> {
            Sussi.getInstance().getSql().addDiscordTextActivity(userId, messagesCount);
        });
    }
}
