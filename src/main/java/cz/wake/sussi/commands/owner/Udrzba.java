package cz.wake.sussi.commands.owner;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.*;

import java.util.Set;

public class Udrzba implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {

    }

    @Override
    public String getCommand() {
        return "udrzba";
    }

    @Override
    public String getDescription() {
        return "Zapnutí/Vypnutí údržby na serverech";
    }

    @Override
    public String getHelp() {
        return ",udrzba <server> - Zapnutí/Vypnutí údržby na serveru";
    }

    @Override
    public CommandType getType() {
        return CommandType.BOT_OWNER;
    }

    @Override
    public Rank getRank() {
        return Rank.BOT_OWNER;
    }
}
