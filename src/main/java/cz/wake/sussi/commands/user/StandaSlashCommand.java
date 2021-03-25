package cz.wake.sussi.commands.user;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.EmoteList;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class StandaSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, SlashCommandEvent event) {
        event.acknowledge(true).queue();
        CommandHook hook = event.getHook();
        hook.setEphemeral(true);

        channel.sendMessage(EmoteList.OLIZNU_TE + EmoteList.CHOCOLATE_BAR).queue();
    }

    @Override
    public String getName() {
        return "standa";
    }

    @Override
    public String getDescription() {
        return "Co na to standa?";
    }

    @Override
    public String getHelp() {
        return "/standa";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
