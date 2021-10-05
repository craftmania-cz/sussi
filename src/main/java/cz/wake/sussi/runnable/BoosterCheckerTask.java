package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class BoosterCheckerTask implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final Guild guild = Sussi.getJda().getGuildById(Sussi.getConfig().getCmGuildID());
        if (guild == null) return;

        SussiLogger.infoMessage("Checking boosters ...");

        final List<Member> realBoosters = guild.getMembersWithRoles(guild.getRoleById(Constants.BOOSTER_ROLE));
        final List<String> dbBoosters = Sussi.getInstance().getSql().getDiscordBoosters();
        for (Member member : realBoosters) {
            if (!dbBoosters.contains(member.getId())) {
                Sussi.getInstance().getSql().updateBooster(member.getId(), 1);
                SussiLogger.greatMessage("New booster: " + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " (" + member.getUser().getId() + ")");
            }
        }

        final List<Member> updatedRealBoosters = guild.getMembersWithRoles(guild.getRoleById(Constants.BOOSTER_ROLE));


        for (String id : dbBoosters) {
            if(!containsBooster(updatedRealBoosters, id)){
                Member member = guild.getMemberById(id);
                SussiLogger.greatMessage("Removing " + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " (" + member.getUser().getId() + ") from boosters in db.");
                Sussi.getInstance().getSql().updateBooster(id, 0);
            }
        }

        SussiLogger.greatMessage("Booster checking finished.");
    }

    private boolean containsBooster(final List<Member> list, final String id){
        return list.stream().anyMatch(member -> member.getId().equals(id));
    }
}