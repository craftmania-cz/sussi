package cz.wake.sussi.listeners;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.VoiceRoom;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

public class MainListener extends ListenerAdapter {

    private EventWaiter w;

    public MainListener(EventWaiter w) {
        this.w = w;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if (e.getAuthor().isBot()) {
            return;
        }

        if (e.getMessage().getContentRaw().startsWith(Sussi.PREFIX) && !e.getAuthor().isBot()) {
            String message = e.getMessage().getContentRaw();
            String command = message.substring(1);
            String[] args = new String[0];
            if (message.contains(" ")) {
                command = command.substring(0, message.indexOf(" ") - 1);

                args = message.substring(message.indexOf(" ") + 1).split(" ");
            }
            for (ICommand cmd : Sussi.getInstance().getCommandHandler().getCommands()) {
                if (cmd.getCommand().equalsIgnoreCase(command) || Arrays.asList(cmd.getAliases()).contains(command)) {
                    SussiLogger.commandMessage("'," + cmd.getCommand() + "', Guild: " + e.getGuild().getName() + ", Channel: " + e.getChannel().getName() + ", Sender: " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " (" + e.getAuthor().getId() + ")");
                    String[] finalArgs = args;
                    EnumSet<Permission> perms = e.getGuild().getSelfMember().getPermissions((GuildChannel) e.getChannel());
                    if (!perms.contains(Permission.MESSAGE_EMBED_LINKS)) {
                        e.getChannel().sendMessage(":warning: | Nemám dostatečná práva na používání EMBED odkazů! Přiděl mi právo: `Vkládání odkazů` nebo `Embed Links`.").queue();
                        return;
                    }
                    if (Rank.getPermLevelForUser(e.getAuthor(), (TextChannel) e.getChannel()).isAtLeast(cmd.getRank())) {
                        try {
                            cmd.onCommand(e.getAuthor(), e.getChannel(), e.getMessage(), finalArgs, e.getMember(), w);
                        } catch (Exception ex) {
                            SussiLogger.fatalMessage("Internal error when executing the command!");
                            ex.printStackTrace();
                        }
                        if (cmd.deleteMessage()) {
                            delete(e.getMessage());
                        }
                    }

                }
            }
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        Sussi.getInstance().getSql().onDisable();
    }

    private void delete(Message message) {
        if (message.getGuildChannel().getGuild().getSelfMember()
                .getPermissions(message.getGuildChannel()).contains(Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }
    }

 /*   @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (event.getMember().getUser().isBot()){
            return;
        }
        Member member = event.getMember();
        member.getGuild().addRoleToMember(member, member.getGuild().getRoleById("761020839683817472")).queue(); // Přidání News role všem novým
    }*/

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if (event.getChannel().getIdLong() == Sussi.getConfig().getNavrhyHlasovaniID() && event.getReaction().getEmoji().getName().equals("\u2705") && event.getUserIdLong() == Sussi.getConfig().getOwnerID()) {
            Sussi.getJda().getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).retrieveMessageById(event.getMessageId()).queue((message -> {
                MessageEmbed napadEmbed = message.getEmbeds().get(0);

                EmbedBuilder embedBuilder = new EmbedBuilder(napadEmbed);
                embedBuilder.setColor(Constants.GREEN);
                embedBuilder.addField("Přidáno", getStringDate(), true);

                message.editMessageEmbeds(embedBuilder.build()).queue();

                Sussi.getJda().getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(Constants.GREEN_MARK + " | Proběhlo přidání nápadu: [**Link**](" + message.getJumpUrl() + ")").build()).queue();
            }));
        }

        if (event.getChannel().getIdLong() == Sussi.getConfig().getNavrhyHlasovaniID() && event.getReaction().getEmoji().getName().equals("\u2611\uFE0F") && event.getUserIdLong() == Sussi.getConfig().getOwnerID()) {
            Sussi.getJda().getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).retrieveMessageById(event.getMessageId()).queue((message -> {
                MessageEmbed napadEmbed = message.getEmbeds().get(0);

                EmbedBuilder embedBuilder = new EmbedBuilder(napadEmbed);
                embedBuilder.setColor(Constants.BLUE);
                embedBuilder.addField("Schváleno", getStringDate(), true);

                message.editMessageEmbeds(embedBuilder.build()).queue();

                Sussi.getJda().getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setDescription(Constants.THUMB_UP + " | Proběhlo schválení nápadu: [**Link**](" + message.getJumpUrl() + ")").build()).queue();
            }));
        }
    }

    private String getStringDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        super.onGuildVoiceJoin(event);
        if(event.getChannelJoined().getIdLong() == Sussi.getConfig().getVytvoritVoiceID()) {
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

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        super.onGuildVoiceLeave(event);
        if(Sussi.getInstance().getSql().getPlayerVoiceOwnerIdByRoomId(event.getChannelLeft().getIdLong()) != 0 && event.getChannelLeft().getMembers().size() == 0) {
            Sussi.getInstance().getSql().deletePlayerVoice(event.getChannelLeft().getIdLong());
            event.getChannelLeft().delete().queue();
        }
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        super.onGuildVoiceMove(event);
        if(Sussi.getInstance().getSql().getPlayerVoiceOwnerIdByRoomId(event.getChannelLeft().getIdLong()) != 0 && event.getChannelLeft().getMembers().size() == 0) {
            Sussi.getInstance().getSql().deletePlayerVoice(event.getChannelLeft().getIdLong());
            event.getChannelLeft().delete().queue();
        }
        if(event.getChannelJoined().getIdLong()  == Sussi.getConfig().getVytvoritVoiceID()) {
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

    @Override
    public void onGuildMemberUpdatePending(@Nonnull GuildMemberUpdatePendingEvent event){
        System.out.println("Call GuildMemberUpdatePendingEvent");
        if(!event.getGuild().getId().equals("207412074224025600")){ // CM
            return;
        }
        System.out.println("State: new pending?: " + event.getNewPending() + ", oldpending: " + event.getOldPending());
        Guild guild = event.getGuild();
        if (!event.getOldPending()) {
            return;
        }
        System.out.println("Adding to user: " + event.getUser().getName() + " news role.");
        Role role = guild.getRoleById(847281784403001375L); // News role
        assert role != null;
        event.getGuild().addRoleToMember(event.getMember(), role).queue();
    }
}
