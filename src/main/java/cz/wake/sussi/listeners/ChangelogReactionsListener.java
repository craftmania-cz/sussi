package cz.wake.sussi.listeners;

import cz.wake.sussi.utils.Constants;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChangelogReactionsListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (!event.getChannel().getId().equals("469461262938669056")) {
            return;
        }

        event.getMessage().addReaction(Constants.THUMB_UP).queue();
        event.getMessage().addReaction(Constants.THUMB_DOWN).queue();
    }
}
