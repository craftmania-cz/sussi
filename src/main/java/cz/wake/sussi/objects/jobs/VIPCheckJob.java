package cz.wake.sussi.objects.jobs;

import cz.wake.sussi.Sussi;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class VIPCheckJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Sussi.getVipManager().recheck();
    }
}
