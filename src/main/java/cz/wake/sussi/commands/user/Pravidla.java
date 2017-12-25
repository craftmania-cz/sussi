package cz.wake.sussi.commands.user;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Pravidla implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Odkaz na pravidla serveru").setDescription("Kompletní pravidla pro náš server nalezneš [**ZDE**](https://wiki.craftmania.cz/zakladni-informace/pravidla)").build()).queue();
    }

    @Override
    public String getCommand() {
        return "pravidla";
    }

    @Override
    public String getDescription() {
        return "Získání odkazu na pravidla serveru.";
    }

    @Override
    public String getHelp() {
        return ",pravidla - Získání dokazu";
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
