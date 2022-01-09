package cz.wake.sussi.objects.jobs;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.SussiLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DailyBonusResetJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SussiLogger.infoMessage("Starting reseting daily bonuses.");
        Sussi.getInstance().getSql().resetDailyBonus();
        SussiLogger.greatMessage("I reseted daily bonuses.");
    }
}
