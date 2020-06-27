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

public class News implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - news :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
            return;
        }

        if(args[0].equals("show")) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Aktuální oznámení na lobby").setDescription(Sussi.getInstance().getSql().getLatestNews()).setColor(Color.CYAN).build()).queue();
            return;
        }

        if(args[0].equals("seen")) {
            if(args.length < 2) {
                channel.sendMessage(MessageUtils.getEmbed().setTitle("Nedostatek argumentů!")
                        .setDescription(getDescription() + "\n\n**Příklad**\n" + getHelp()).build()).queue();
                return;
            }
            if(!Sussi.getInstance().getSql().existsPlayer(args[1])) {
                MessageUtils.sendErrorMessage("Hráč `" + args[1] + "` nebyl nalezen.", channel);
                return;
            }
            channel.sendMessage(MessageUtils.getEmbed().setTitle("News").setDescription("Hráč " + args[1] + " si " + (Sussi.getInstance().getSql().sawLatestNews(args[1]) ? "**zobrazil**" : "**nezobrazil**") + " oznámení na lobby.").setColor(Color.yellow).build()).queue();
            return;
        }

        if(args[0].equals("update")) {
            if(sender.getId().equals(Sussi.getConfig().getOwnerID())) {
                if(args.length < 2) {
                    channel.sendMessage(MessageUtils.getEmbed().setTitle("Nedostatek argumentů!")
                            .setDescription(getDescription() + "\n\n**Příklad**\n" + getHelp()).build()).queue();
                    return;
                }
                String msg = message.getContentRaw().substring(13);
                Sussi.getInstance().getSql().updateLatestNews(msg);
                Sussi.getInstance().getSql().resetNewsReads();
                channel.sendMessage(MessageUtils.getEmbed().setDescription("Na lobby bylo nastaveno nové oznámení a všem bylo resetováno zobrazení. Nová zpráva: ``" + msg + "``").setColor(Color.GREEN).build()).queue();
            } else {
                MessageUtils.sendErrorMessage("Na toto má práva pouze Kwak!", channel);
                return;
            }
        }

        if(args[0].equals("reset")) {
            if(sender.getId().equals(Sussi.getConfig().getOwnerID())) {
                Sussi.getInstance().getSql().resetNewsReads();
                channel.sendMessage(MessageUtils.getEmbed().setDescription("Všem bylo resetováno zobrazení oznámení na lobby.").setColor(Color.GREEN).build()).queue();
            } else {
                MessageUtils.sendErrorMessage("Na toto má práva pouze Kwak!", channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "news";
    }

    @Override
    public String getDescription() {
        return "Zobrazí, zda hráč přečetl oznámení, nastaví novou zprávu nebo všem resetuje přečtení zprávy.";
    }

    @Override
    public String getHelp() {
        return ",news show - Zobrazí aktuální oznámení na lobby" +
                ",news seen [nick] - Vypíše, zda si hráč zobrazil oznámení na lobby.\n" +
                ",news update [text] - Nastaví nové oznámení na lobby.\n" +
                ",news reset - Resetuje všem zobrazení oznámení na lobby (Jen wake a krosta)";
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
