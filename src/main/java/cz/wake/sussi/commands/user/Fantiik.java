package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.EmoteList;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Fantiik implements ICommand {

    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(EmoteList.ORANGE + " -> " + EmoteList.GLASS_OF_ORANGE).queue();
    }

    public String getCommand() {
        return "fantiik";
    }

    public String getDescription() {
        return "Návod na fantu!";
    }

    public String getHelp() {
        return ",fantiik";
    }

    public CommandType getType() {
        return CommandType.FUN;
    }

    public Rank getRank() {
        return Rank.USER;
    }
}
