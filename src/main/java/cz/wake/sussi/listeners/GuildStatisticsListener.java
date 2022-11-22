package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GuildStatisticsListener extends ListenerAdapter {

    private final HashMap<String, Long> voiceLog = new HashMap<>();
    public static final HashMap<String, Long> messageLog = new HashMap<>();

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null && event.getChannelLeft() == null) { // Voice join
            final Member member = event.getMember();
            if (member.getUser().isBot()) return;
            voiceLog.put(member.getId(), System.currentTimeMillis());
        }
        if (event.getChannelJoined() == null && event.getChannelLeft() != null) { // Voice leave
            final Member member = event.getMember();
            if (member.getUser().isBot()) return;
            final Long timeSpent = System.currentTimeMillis() - voiceLog.getOrDefault(member.getId(), 0L);
            SussiLogger.infoMessage("Updating voice activity for user " + member.getUser().getAsTag());
            Sussi.getInstance().getSql().addDiscordVoiceActivity(member.getId(), timeSpent);
            voiceLog.remove(member.getId());
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final Member member = event.getMember();

        if (member == null) return;
        if (member.getUser().isBot()) return;

        messageLog.put(member.getId(), messageLog.getOrDefault(member.getId(), 0L) + 1L);
    }
}
