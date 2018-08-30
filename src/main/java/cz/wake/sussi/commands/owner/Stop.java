package cz.wake.sussi.commands.owner;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Stop implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(sender.getId().equals("177516608778928129")){
            channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setDescription(":skull_crossbones: | Vypínám se...").build()).queue();
            System.exit(0);
        }
    }

    @Override
    public String getCommand() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Vypnutí Sussi";
    }

    @Override
    public String getHelp() {
        return ".stop - Vypnutí";
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
