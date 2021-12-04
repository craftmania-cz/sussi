package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
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

        SussiLogger.infoMessage("Checking booster roles...");

        guild.getRoles().stream().filter(role -> role.getName().startsWith("#")).forEach(role -> {
            final List<Member> members = guild.getMembersWithRoles(role);
            if (members.isEmpty()) {
                SussiLogger.infoMessage("Role " + role.getName() + " is empty. Deleting..");
                role.delete().queue();
                return;
            }
            for (Member member : members) {
                if (!member.getRoles().contains(member.getGuild().getRoleById(Constants.BOOSTER_ROLE))) {
                    // Delete all booster roles
                    guild.removeRoleFromMember(member, role).queue(success -> {
                        if (guild.getMembersWithRoles(role).isEmpty()) {
                            role.delete().queue();
                        }
                    });
                    member.getUser().openPrivateChannel().queue(channel -> {
                        channel.sendMessageEmbeds(MessageUtils.getEmbedError().setTitle("Přestal jsi boostovat CraftMania server").setDescription("Z tohoto důvodu ti byla odebrána role `" + role.getName() + "` tvé barvy.").build()).queue();
                    });
                }
            }
        });

        SussiLogger.greatMessage("Booster roles checking finished.");
    }
}
