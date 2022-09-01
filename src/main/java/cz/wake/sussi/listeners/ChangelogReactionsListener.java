package cz.wake.sussi.listeners;

import cz.wake.sussi.utils.Constants;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChangelogReactionsListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.getChannel().getId().equals("469461262938669056")) {
            return;
        }

        event.getMessage().addReaction(Emoji.fromUnicode(Constants.THUMB_UP)).queue();
        event.getMessage().addReaction(Emoji.fromUnicode(Constants.THUMB_DOWN)).queue();
    }
}
