package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
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

public class Napad implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {

        if (Sussi.getJda().getGuildById("207412074224025600").getTextChannelById("648576105619521537") != channel) {
            MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(Constants.RED).setDescription("Tento příkaz lze použít pouze v #navrhy_diskuze").build(), 5000, channel);
            return;
        }

        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - napad :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
            return;
        }

        MessageBuilder msg = new MessageBuilder();
        String description = message.getContentRaw().substring(message.getContentRaw().indexOf(" "));
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Hlasování o nápadu");
        embed.setColor(Color.PINK);
        embed.setDescription(description);
        embed.setFooter("Navrhl(a): " + member.getEffectiveName(), sender.getAvatarUrl());
        msg.setEmbed(embed.build());
        Sussi.getJda().getGuildById("207412074224025600").getTextChannelById("651205289894215728").sendMessage(msg.build()).queue(m -> {
            m.addReaction(Constants.THUMB_UP).queue();
            m.addReaction(Constants.THUMB_DOWN).queue();
        });

        MessageUtils.sendAutoDeletedMessage(member.getAsMention() + " tvůj nápad byl přidán do #navrhy_hlasovani", 3000, channel);
    }

    @Override
    public String getCommand() {
        return "napad";
    }

    @Override
    public String getDescription() {
        return "Přidání nápadu do channelu #navrhy_hlasovani";
    }

    @Override
    public String getHelp() {
        return ",napad [text]";
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
        return new String[]{"idea"};
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
