package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild() == null || event.getUser().isBot()) {
            return;
        }
        for (ISlashCommand slashCommand : Sussi.getSlashCommandHandler().getSlashCommands()) {
            if (slashCommand.getName().equals(event.getName())) {
                if (Rank.getPermLevelForUser(event.getUser(), (TextChannel) event.getChannel()).isAtLeast(slashCommand.getRank())) {
                    event.deferReply().queue();
                    try {
                        slashCommand.onSlashCommand(event.getUser(), event.getChannel(), event.getMember(), event.getHook(), event);
                    } catch (Exception e) {
                        SussiLogger.fatalMessage("Internal error when executing the command!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
