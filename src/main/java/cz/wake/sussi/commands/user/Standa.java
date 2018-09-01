package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.EmoteList;
import net.dv8tion.jda.core.entities.*;

public class Standa implements ICommand {

    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(EmoteList.OLIZNU_TE + EmoteList.CHOCOLATE_BAR).queue();
    }

    public String getCommand() {
        return "standa";
    }

    public String getDescription() {
        return "Co na to standa?";
    }

    public String getHelp() {
        return ",standa";
    }

    public CommandType getType() {
        return CommandType.FUN;
    }

    public Rank getRank() {
        return Rank.USER;
    }
}
