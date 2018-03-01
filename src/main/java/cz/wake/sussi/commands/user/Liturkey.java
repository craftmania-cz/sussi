package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Liturkey implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(":turkey: + :fire: = <:liturkey:413663313117970432>").queue();
    }

    @Override
    public String getCommand() {
        return "liturkey";
    }

    @Override
    public String getDescription() {
        return "Návod na upečení krocana!";
    }

    @Override
    public String getHelp() {
        return ",liturkey";
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
