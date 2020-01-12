package cz.wake.sussi.commands.mod;

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

public class BlockCountry implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (sender.getId().equals("238410025813540865") || sender.getId().equals("177516608778928129")) {

            if (Boolean.parseBoolean(Sussi.getInstance().getSql().getCraftBungeeConfigValue("block_country"))) {
                Sussi.getInstance().getSql().updateCraftBungeeConfigValue("block_country", "false");
                channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Příkaz byl úspěsně vykonán").setDescription("BlockCountry byl vypnut").build()).queue();
            } else {
                Sussi.getInstance().getSql().updateCraftBungeeConfigValue("block_country", "true");
                channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Příkaz byl úspěsně vykonán").setDescription("BlockCountry byl zapnut").build()).queue();
            }
        }
    }

    @Override
    public String getCommand() {
        return "blockcountry";
    }

    @Override
    public String getDescription() {
        return "Nastaveni, jestli server bude blokovat pripojeni z jinych zemi nez CZ/SK";
    }

    @Override
    public String getHelp() {
        return ",blockcountry";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}
