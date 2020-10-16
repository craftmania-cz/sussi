package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.List;

public class EmptyVoiceCheckTask implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        List<VoiceChannel> voiceChannels = Sussi.getJda().getGuildById(Constants.CM_GUILD_ID).getCategoryById(Constants.CATEGORY_KECARNA_ID).getVoiceChannels();
        voiceChannels.forEach((voiceChannel -> {
            if (voiceChannel.getMembers().size() == 0 && !voiceChannel.getId().equals(Constants.VOICE_CREATE_ID)) {
                SussiLogger.infoMessage("Kanal " + voiceChannel.getName() + " byl smazán, jelikož je prázdný.");
                voiceChannel.delete().queue();
            }
        }));
    }
}
