package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.ServerInfo;
import net.dv8tion.jda.api.entities.Activity;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class StatusChangerTask implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //Sussi.getJda().getPresence().setActivity(Activity.listening(ServerInfo.getOnlinePlayers() + " hráčů"));
        Sussi.getJda().getPresence().setActivity(Activity.competing("Minecraft"));
    }
}
