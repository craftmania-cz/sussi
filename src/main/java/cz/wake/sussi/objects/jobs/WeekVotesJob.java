package cz.wake.sussi.objects.jobs;

import cz.wake.sussi.Sussi;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WeekVotesJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Sussi.getVoteManager().resetWeek();
    }
}
