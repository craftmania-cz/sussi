package cz.wake.sussi.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public interface ISlashCommand {

    void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event);

    String getName();

    @Deprecated
    String getDescription();

    @Deprecated
    String getHelp();

    @Deprecated
    CommandType getType();

    Rank getRank();

    default boolean defferReply() {
        return true;
    }

    default boolean isEphemeral() {
        return false;
    }
}
