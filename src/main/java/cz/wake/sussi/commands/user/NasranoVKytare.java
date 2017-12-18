package cz.wake.sussi.commands.user;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class NasranoVKytare implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage("<:kubrastig:252501940791934977> :poop: :guitar:").queue();
    }

    @Override
    public String getCommand() {
        return "nasranovkytare";
    }

    @Override
    public String getDescription() {
        return "Má Kubrastig nasráno v kytaře?";
    }

    @Override
    public String getHelp() {
        return ",nasranovkytare";
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
