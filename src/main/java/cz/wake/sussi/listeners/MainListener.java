package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.VoiceRoom;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.EnumSet;

public class MainListener extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        Sussi.getInstance().getSql().onDisable();
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null && event.getChannelLeft() == null) { // Voice join -> create
            if (!event.getChannelJoined().getParentCategory().getId().equals(Constants.CATEGORY_KECARNA_ID)) {
                return;
            }
            if (event.getChannelJoined().getIdLong() == Sussi.getConfig().getVytvoritVoiceID()) {
                if(Sussi.getInstance().getSql().getPlayerVoiceOwnerIdByRoomId(event.getMember().getIdLong()) == 0) {
                    VoiceRoom vr = Sussi.getInstance().getSql().getVoiceRoom(event.getMember().getId());

                    String vrName;
                    String vrAddedMembers;
                    String vrBannedMembers;

                    if (vr.getName().equals("default")) {
                        vrName = event.getMember().getUser().getName();
                    } else {
                        vrName = vr.getName();
                    }

                    event.getGuild().getCategoryById(Constants.CATEGORY_KECARNA_ID).createVoiceChannel(vrName).setUserlimit(Math.toIntExact(vr.getLimit())).setBitrate(vr.getBitrate() * 1000).queue(voiceChannel -> {
                        Sussi.getInstance().getSql().createNewPlayerVoice(event.getMember().getIdLong(), voiceChannel.getIdLong());
                        if (vr.getLocked()) {
                            voiceChannel.getManager().putPermissionOverride(event.getMember().getGuild().getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
                        }
                        for (String memberId : vr.getBannedMembers()){
                            Member member = event.getGuild().getMemberById(memberId);
                            if (member == null) continue;
                            voiceChannel.getManager().getChannel().getManager().putPermissionOverride(member, null, EnumSet.of(Permission.VIEW_CHANNEL)).queue();
                        }
                        for (String memberId : vr.getAddedMembers()){
                            Member member = event.getGuild().getMemberById(memberId);
                            if (member == null) continue;
                            voiceChannel.getManager().getChannel().getManager().putPermissionOverride(member, EnumSet.of(Permission.VOICE_CONNECT), null).queue();
                        }
                        voiceChannel.getManager().putPermissionOverride(event.getMember(), EnumSet.of(Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL), null).queue();
                        event.getGuild().moveVoiceMember(event.getMember(), voiceChannel).queue();
                        event.getGuild().getTextChannelById(Constants.CHANNEL_BOT_COMMANDS_ID).sendMessage(event.getMember().getAsMention() + " tvůj kanál byl vytvořen, můžeš ho spravovat pomocí příkazu `/room info` nebo `/room help`").queue();
                    });
                } else {
                    event.getGuild().kickVoiceMember(event.getMember()).queue();
                }
            }
        }
        if (event.getChannelJoined() == null && event.getChannelLeft() != null) { // Opustil kanál
            if (!event.getChannelLeft().getParentCategory().getId().equals(Constants.CATEGORY_KECARNA_ID)) {
                return;
            }
            if (event.getChannelLeft().getMembers().size() == 0 && event.getChannelLeft().getIdLong() != Sussi.getConfig().getVytvoritVoiceID()) {
                Sussi.getInstance().getSql().deletePlayerVoice(event.getChannelLeft().getIdLong());
                event.getChannelLeft().delete().queue();
            }
        }
        if (event.getChannelJoined() != null && event.getChannelLeft() != null) { // Move action
            if (!event.getChannelLeft().getParentCategory().getId().equals(Constants.CATEGORY_KECARNA_ID)) {
                return;
            }
            if (event.getChannelLeft().getMembers().size() == 0 && event.getChannelLeft().getIdLong() != Sussi.getConfig().getVytvoritVoiceID()) { // Opustil jeho vlastní kanál
                Sussi.getInstance().getSql().deletePlayerVoice(event.getChannelLeft().getIdLong());
                event.getChannelLeft().delete().queue();
            }
            if (event.getChannelJoined().getIdLong() == Sussi.getConfig().getVytvoritVoiceID()) { // Přepojil se z svého voice znovu do create kanálu
                String name = event.getMember().getUser().getName();
                if(Sussi.getInstance().getSql().getPlayerVoiceOwnerIdByRoomId(event.getMember().getIdLong()) == 0) {
                    event.getGuild().getCategoryById(Constants.CATEGORY_KECARNA_ID).createVoiceChannel(name).queue(voiceChannel -> {
                        Sussi.getInstance().getSql().createNewPlayerVoice(event.getMember().getIdLong(), voiceChannel.getIdLong());
                        voiceChannel.getManager().putPermissionOverride(event.getMember(), EnumSet.of(Permission.VOICE_CONNECT, Permission.VIEW_CHANNEL), null).queue();
                        event.getGuild().moveVoiceMember(event.getMember(), voiceChannel).queue();
                        event.getGuild().getTextChannelById(Constants.CHANNEL_BOT_COMMANDS_ID).sendMessage(event.getMember().getAsMention() + " tvůj kanál byl vytvořen, můžeš ho spravovat pomocí příkazu `/room info` nebo `/room help`").queue();
                    });
                } else {
                    event.getGuild().kickVoiceMember(event.getMember()).queue();
                }
            }
        }
    }

    @Override
    public void onGuildMemberUpdatePending(@NotNull GuildMemberUpdatePendingEvent event){
        if(!event.getGuild().getId().equals("207412074224025600")){ // CM
            return;
        }
        Guild guild = event.getGuild();
        if (!event.getOldPending()) {
            return;
        }
        SussiLogger.infoMessage("User joined: " + event.getUser().getName());
        Role role = guild.getRoleById(847281784403001375L); // News role
        assert role != null;
        event.getGuild().addRoleToMember(event.getMember(), role).queue();
    }
}
