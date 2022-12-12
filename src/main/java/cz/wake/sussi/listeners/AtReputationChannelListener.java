package cz.wake.sussi.listeners;

import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * AT Reputation handler -> maže zprávy, z kanálu #at_reputation
 * Jelikož tam se mají dát psát jenom příkazy na AT hodnocení.
 */
public class AtReputationChannelListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getGuild().getId().equals(Constants.CM_GUILD_ID)) {
            return;
        }
        if (event.getChannel().getIdLong() != Constants.CM_AT_REPUTATION_CHANNEL) {
            return;
        }
        event.getMessage().delete().queue((success -> {
            SussiLogger.warnMessage("[AT-REPUTATION HANDLER] Deleted message from " + event.getMessage().getAuthor().getAsTag() + " with text: " + event.getMessage().getContentRaw());
        }));
    }
}
