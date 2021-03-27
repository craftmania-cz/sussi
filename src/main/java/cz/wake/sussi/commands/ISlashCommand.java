package cz.wake.sussi.commands;

import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface ISlashCommand {

    void onSlashCommand(User sender, MessageChannel channel, Member member, CommandHook hook, SlashCommandEvent event);

    String getName();

    String getDescription();

    String getHelp();

    CommandType getType();

    Rank getRank();
}
