package cz.wake.sussi.utils;

import cz.wake.sussi.Sussi;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.io.ByteArrayInputStream;

public class MessageUtils {

    public static void editMessage(Message message, String content) {
        message.editMessage(content).queue();
    }

    public static Message sendFile(MessageChannel channel, String s, String fileContent, String filename) {
        ByteArrayInputStream stream = new ByteArrayInputStream(fileContent.getBytes());
        return channel.sendFile(stream, filename, new MessageBuilder().append(s).build()).complete();
    }

    public static String getTag(User user) {
        return user.getName() + '#' + user.getDiscriminator();
    }

    public static EmbedBuilder getEmbed(User user, Color c) {
        return getEmbed(c).setFooter("Požadavek od @" + getTag(user), user.getEffectiveAvatarUrl());
    }

    public static EmbedBuilder getEmbed(Color c) {
        return new EmbedBuilder().setColor(c);
    }

    public static EmbedBuilder getEmbed(){ return new EmbedBuilder(); }

    public static String getAvatar(User user) {
        return user.getEffectiveAvatarUrl();
    }

    public static String getDefaultAvatar(User user) {
        return user.getDefaultAvatarUrl();
    }

    //TODO: Dodelat try
    public static Message sendErrorMessage(EmbedBuilder builder, MessageChannel channel) {
        return channel.sendMessage(builder.setColor(Constants.RED).build()).complete();
    }

    public static Message sendErrorMessage(String message, MessageChannel channel) {
        return channel.sendMessage(MessageUtils.getEmbed().setColor(Constants.RED).setDescription(message).build())
                .complete();
    }

    public static void editMessage(EmbedBuilder embed, Message message) {
        editMessage(message.getContentRaw(), embed, message);
    }

    public static void editMessage(String s, EmbedBuilder embed, Message message) {
        if (message != null)
            message.editMessage(new MessageBuilder().append(s).setEmbed(embed.build()).build()).queue();
    }

    public static EmbedBuilder getEmbedError() {
        return new EmbedBuilder().setFooter("Chyba při provádění akce CorgiBot", Sussi.getJda().getSelfUser().getAvatarUrl());
    }
}
