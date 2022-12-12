package cz.wake.sussi.listeners;

import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class NewsThreadListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getGuild().getId().equals(Constants.CM_GUILD_ID)) {
            return;
        }
        if (event.getChannel().getIdLong() != Constants.CM_NEWS_CHANNEL) {
            return;
        }
        event.getMessage().createThreadChannel("Diskuze k tématu").queue();
        SussiLogger.greatMessage("Thread created in channel #oznameni");
    }
}
