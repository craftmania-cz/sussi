package cz.wake.sussi.utils;

import cz.wake.sussi.Sussi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class MessageUtils {

    public static void editMessage(Message message, String content) {
        message.editMessage(content).queue();
    }

    /*public static Message sendFile(MessageChannel channel, String s, String fileContent, String filename) {
        ByteArrayInputStream stream = new ByteArrayInputStream(fileContent.getBytes());
        return channel.sendFile(stream, filename, new MessageBuilder().append(s).build()).complete();
    }*/

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
        return channel.sendMessageEmbeds(builder.setColor(Constants.RED).build()).complete();
    }

    public static Message sendErrorMessage(EmbedBuilder builder, InteractionHook hook) {
        return hook.editOriginalEmbeds(builder.setColor(Constants.RED).build()).complete();
    }

    public static Message sendErrorMessage(String message, MessageChannel channel) {
        return channel.sendMessageEmbeds(MessageUtils.getEmbed().setColor(Constants.RED).setDescription(message).build())
                .complete();
    }

    // TODO: Dopsat všechny metody aby se dali posílat hooku
    public static Message sendErrorMessage(String message, InteractionHook hook) {
        return hook.editOriginalEmbeds(MessageUtils.getEmbed().setColor(Constants.RED).setDescription(message).build()).complete();
    }

    public static Message sendMessage(Color color, String message, MessageChannel channel) {
        return channel.sendMessageEmbeds(MessageUtils.getEmbed().setColor(color).setDescription(message).build()).complete();
    }

    public static EmbedBuilder getEmbedError() {
        return new EmbedBuilder().setColor(Constants.RED);
    }

    private static void autoDeleteMessage(Message message, long delay) {
        message.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
    }

    public static void sendAutoDeletedMessage(String message, long delay, MessageChannel channel) {
        channel.sendMessage(message).queue(msg -> autoDeleteMessage(msg, delay));
    }

    public static void sendAutoDeletedMessage(MessageEmbed messageEmbed, long delay, MessageChannel channel) {
        channel.sendMessageEmbeds(messageEmbed).queue(msg -> autoDeleteMessage(msg, delay));
    }
}
