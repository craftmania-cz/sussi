package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class Unlink implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (!Sussi.getInstance().getSql().isConnectedToMC(sender.getId())) {
            MessageUtils.sendErrorMessage("Tento účet není propojen se žádnym MC účtem!", channel);
            return;
        }

        channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Účet úspěšně odpojen").setDescription("Tvůj discord profil byl odpojen od MC účtu " + Sussi.getInstance().getSql().getMinecraftNick(sender.getId()) + "!").build()).queue();
        Sussi.getInstance().getSql().disconnectFromMC(sender.getId());
    }

    @Override
    public String getCommand() {
        return "unlink";
    }

    @Override
    public String getDescription() {
        return "Odpojení discord profilu z MC účtu.";
    }

    @Override
    public String getHelp() {
        return ",unlink - odpojení discord profilu";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
