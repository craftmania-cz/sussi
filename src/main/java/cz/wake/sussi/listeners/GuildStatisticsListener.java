package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GuildStatisticsListener extends ListenerAdapter {

    private final HashMap<String, Long> voiceLog = new HashMap<>();
    public static final HashMap<String, Long> messageLog = new HashMap<>();

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        final Member member = event.getMember();

        if (member.getUser().isBot()) return;

        voiceLog.put(member.getId(), System.currentTimeMillis());
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        final Member member = event.getMember();

        if (member.getUser().isBot()) return;

        final Long timeSpent = System.currentTimeMillis() - voiceLog.getOrDefault(member.getId(), 0L);

        SussiLogger.infoMessage("Updating voice activity for user " + member.getUser().getAsTag() + "...");
        Sussi.getInstance().getSql().addDiscordVoiceActivity(member.getId(), timeSpent);

        voiceLog.remove(member.getId());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final Member member = event.getMember();

        if (member == null) return;
        if (member.getUser().isBot()) return;

        messageLog.put(member.getId(), messageLog.getOrDefault(member.getId(), 0L) + 1L);
    }
}
