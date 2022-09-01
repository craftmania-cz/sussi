package cz.wake.sussi.listeners;

import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CraftManiaArchiveListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }

        // CM Archive discord
        if (!event.getGuild().getId().equals("678409004573130772")) {
            return;
        }

        // Generald & Requests channely
        if (event.getChannel().getId().equals("678409004573130775") || event.getChannel().getId().equals("678412731828928522")) {
            return;
        }

        Message message = event.getMessage();

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

        MessageUtils.sendAutoDeletedMessage( event.getMember().getAsMention() + " lze vkládat pouze obsah a k němu popisek!", 5000, event.getChannel());
        event.getMessage().delete().queue();


    }
}
