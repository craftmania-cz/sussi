package cz.wake.sussi.listeners;

import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CraftManiaArchiveListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {

        if (e.getAuthor().isBot()) {
            return;
        }

        // CM Archive discord
        if (!e.getGuild().getId().equals("678409004573130772")) {
            return;
        }

        // Generald & Requests channely
        if (e.getChannel().getId().equals("678409004573130775") || e.getChannel().getId().equals("678412731828928522")) {
            return;
        }

        Message message = e.getMessage();

        if (message.getAttachments().size() > 0) {
            return;
        }

        if ((message.getContentRaw().contains("prntscr.com")
                || message.getContentRaw().contains("prnt.sc")
                || message.getContentRaw().contains("imgur.com")
                || message.getContentRaw().contains("youtu.be")
                || message.getContentRaw().contains("youtube.com"))) {
            return;
        }

        MessageUtils.sendAutoDeletedMessage( e.getMember().getAsMention() + " lze vkládat pouze obsah a k němu popisek!", 5000, e.getChannel());
        e.getMessage().delete().queue();


    }
}
