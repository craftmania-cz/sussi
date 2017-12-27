package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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

        if (e.getMessage().getRawContent().startsWith(String.valueOf(Sussi.PREFIX)) && !e.getAuthor().isBot()) {
            String message = e.getMessage().getRawContent();
            String command = message.substring(1);
            String[] args = new String[0];
            if (message.contains(" ")) {
                command = command.substring(0, message.indexOf(" ") - 1);

                args = message.substring(message.indexOf(" ") + 1).split(" ");
            }
            for (ICommand cmd : Sussi.getInstance().getCommandHandler().getCommands()) {
                if (cmd.getCommand().equalsIgnoreCase(command)) {
                    String[] finalArgs = args;
                    List<Permission> perms = e.getGuild().getSelfMember().getPermissions(e.getChannel());
                    if (!perms.contains(Permission.MESSAGE_EMBED_LINKS)) {
                        e.getChannel().sendMessage(":warning: | Nemám dostatečná práva na používání EMBED odkazů! Přiděl mi právo: `Vkládání odkazů` nebo `Embed Links`.").queue();
                        return;
                    }
                    if (Rank.getPermLevelForUser(e.getAuthor(), e.getChannel()).isAtLeast(cmd.getRank())) {
                        try {
                            cmd.onCommand(e.getAuthor(), e.getChannel(), e.getMessage(), finalArgs, e.getMember(), w);
                        } catch (Exception ex) {
                            //
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
}
