package cz.wake.sussi.objects.votes;

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
