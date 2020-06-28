package cz.wake.sussi.listeners;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

public class MainListener extends ListenerAdapter {

    private EventWaiter w;

    public MainListener(EventWaiter w) {
        this.w = w;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

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
                if (cmd.getCommand().equalsIgnoreCase(command)) {
                    SussiLogger.commandMessage("'," + cmd.getCommand() + "', Guild: " + e.getGuild().getName() + ", Channel: " + e.getChannel().getName() + ", Sender: " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " (" + e.getAuthor().getId() + ")");
                    String[] finalArgs = args;
                    EnumSet<Permission> perms = e.getGuild().getSelfMember().getPermissions(e.getChannel());
                    if (!perms.contains(Permission.MESSAGE_EMBED_LINKS)) {
                        e.getChannel().sendMessage(":warning: | Nemám dostatečná práva na používání EMBED odkazů! Přiděl mi právo: `Vkládání odkazů` nebo `Embed Links`.").queue();
                        return;
                    }
                    if (Rank.getPermLevelForUser(e.getAuthor(), e.getChannel()).isAtLeast(cmd.getRank())) {
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
        if (message.getTextChannel().getGuild().getSelfMember()
                .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if(event.getChannel().getIdLong() == Sussi.getConfig().getNavrhyHlasovaniID() && event.getReaction().getReactionEmote().getName().equals("\u2705") && event.getUserIdLong() == Sussi.getConfig().getOwnerID()) {
            Sussi.getJda().getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).retrieveMessageById(event.getMessageId()).queue((message -> {
                MessageEmbed napadEmbed = message.getEmbeds().get(0);

                EmbedBuilder embedBuilder = new EmbedBuilder(napadEmbed);
                embedBuilder.setColor(Constants.GREEN);
                embedBuilder.addField("Přidáno", getStringDate(), true);

                message.editMessage(embedBuilder.build()).queue();

                Sussi.getJda().getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(Constants.GREEN_MARK + " | Proběhlo přidání nápadu: [**Link**](" + message.getJumpUrl() + ")").build()).queue();
            }));
        }

        if(event.getChannel().getIdLong() == Sussi.getConfig().getNavrhyHlasovaniID() && event.getReaction().getReactionEmote().getName().equals("\u2611\uFE0F") && event.getUserIdLong() == Sussi.getConfig().getOwnerID()) {
            Sussi.getJda().getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).retrieveMessageById(event.getMessageId()).queue((message -> {
                MessageEmbed napadEmbed = message.getEmbeds().get(0);

                EmbedBuilder embedBuilder = new EmbedBuilder(napadEmbed);
                embedBuilder.setColor(Constants.BLUE);
                embedBuilder.addField("Schváleno", getStringDate(), true);

                message.editMessage(embedBuilder.build()).queue();

                Sussi.getJda().getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).sendMessage(MessageUtils.getEmbed(Constants.BLUE).setDescription(Constants.THUMB_UP + " | Proběhlo schválení nápadu: [**Link**](" + message.getJumpUrl() + ")").build()).queue();
            }));
        }
    }

    private String getStringDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
