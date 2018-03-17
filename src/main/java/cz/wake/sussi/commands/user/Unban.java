package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Unban implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Formulář na unban").setDescription("Pokuď chceš unban, musíš si o to požádat [**ZDE**](https://craftmania.cz/forms/zadost-o-unban.6/respond)").setFooter("K odeslání žádosti musíš být na webu registrovaný!", null).build()).queue();
    }

    @Override
    public String getCommand() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return "Získání odkazu na unban formulář.";
    }

    @Override
    public String getHelp() {
        return ",unban - Získání dokazu";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}