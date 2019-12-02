package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class Poll implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (!sender.getId().equals("177516608778928129") && !sender.getId().equals("238410025813540865")) {
            channel.sendMessage("Na toto mají právo pouze Krosta a Kwak!").queue();
            return;
        }

        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - poll :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
            return;
        }
        boolean here = false;
        if (args.length > 1) {
            here = message.getContentRaw().substring(message.getContentRaw().lastIndexOf(" ") + 1).equals("-h");
            MessageBuilder msg = new MessageBuilder();
            String description = message.getContentRaw().substring(message.getContentRaw().indexOf(" "));
            if (here) {
                msg.append("@here");
                description = description.substring(0, description.lastIndexOf("-h"));
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Dotaznîk");
            embed.setColor(Color.YELLOW);
            embed.setDescription(description);
            msg.setEmbed(embed.build());
            channel.sendMessage(msg.build()).queue(m -> {
                m.addReaction(Constants.GREEN_MARK).queue();
                m.addReaction(Constants.CROSS_MARK).queue();
            });
        }
    }

    @Override
    public String getCommand() {
        return "poll";
    }

    @Override
    public String getDescription() {
        return "Vytvoří poll s reakcemi.";
    }

    @Override
    public String getHelp() {
        return ",poll [text] [-h] - Vytvoří poll s určeným textem + [-h] označí @here";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
