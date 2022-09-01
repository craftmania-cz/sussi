package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Napad implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {

        if (Sussi.getJda().getGuildById(Sussi.getConfig().getCmGuildID()).getTextChannelById(Sussi.getConfig().getNavrhyDiskuzeID()) != channel) {
            MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(Constants.RED).setDescription("Tento příkaz lze použít pouze v #navrhy_diskuze").build(), 5000, channel);
            return;
        }

        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - napad :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
            return;
        }

        String description = message.getContentRaw().substring(message.getContentRaw().indexOf(" "));
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Hlasování o nápadu");
        embed.setColor(Color.PINK);
        embed.setDescription(description);
        if(message.getAttachments().size() > 0 && message.getAttachments().get(0).isImage()) {
            Message.Attachment attachment = message.getAttachments().get(0);
            String filename = attachment.getFileName();
            File temp = new File(filename);
            try {
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            attachment.downloadToFile(temp);
            embed.setImage("attachment://"+filename);
            Sussi.getJda().getGuildById(Sussi.getConfig().getCmGuildID()).getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).sendFile(temp, filename).setEmbeds(embed.build()).queue(m -> {
                m.addReaction(Emoji.fromUnicode(Constants.THUMB_UP)).queue();
                m.addReaction(Emoji.fromUnicode(Constants.THUMB_DOWN)).queue();
            });
            temp.delete();
        } else {
            Sussi.getJda().getGuildById(Sussi.getConfig().getCmGuildID()).getTextChannelById(Sussi.getConfig().getNavrhyHlasovaniID()).sendMessageEmbeds(embed.build()).queue(m -> {
                m.addReaction(Emoji.fromUnicode(Constants.THUMB_UP)).queue();
                m.addReaction(Emoji.fromUnicode(Constants.THUMB_DOWN)).queue();
            });
        }

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
        return new String[]{"idea", "navrh"};
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
