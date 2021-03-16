package cz.wake.sussi.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface ISlashCommand {

    void onSlashCommand(SlashCommandEvent event);
}
