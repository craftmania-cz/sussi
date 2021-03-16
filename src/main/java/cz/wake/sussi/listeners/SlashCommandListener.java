package cz.wake.sussi.listeners;

import cz.wake.sussi.commands.slash.HelpSlashCommand;
import cz.wake.sussi.commands.slash.LinkSlashCommand;
import cz.wake.sussi.commands.slash.UnlinkSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild() == null) {
            return;
        }
        switch (event.getName()) {
            case "help":
                new HelpSlashCommand().call(event);
                break;
            case "unlink":
                new UnlinkSlashCommand().call(event);
                break;
            case "link":
                new LinkSlashCommand().call(event);
                break;
        }
    }

}
