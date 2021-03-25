package cz.wake.sussi.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface ISlashCommand {

    void onSlashCommand(User sender, MessageChannel channel, Member member, SlashCommandEvent event);

    String getName();

    String getDescription();

    String getHelp();

    CommandType getType();

    Rank getRank();
}