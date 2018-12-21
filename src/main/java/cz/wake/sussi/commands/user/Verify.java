package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.ConnectTask;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Verify implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (Sussi.getInstance().getSql().isAlreadyLinked(sender.getId())) {
            channel.sendMessage(MessageUtils.getEmbed(Color.RED)
                    .setDescription("Tento účet je již přepojen s nickem `" + Sussi.getInstance().getSql().getLinkedNickname(sender.getId()) + "`.")
                    .build()).queue();
            return;
        }
        if (!Sussi.getInstance().getSql().hasConnection(sender.getId())) {
            String code = getSaltString();
            ConnectTask t = Sussi.getInstance().getSql().createConnectionTask(sender.getId(), code);
            channel.sendMessage(MessageUtils.getEmbed(Color.GREEN)
                .setDescription("Byl ti poslán speciální kód na ověření do DM!")
            .build()).queue();
            sender.openPrivateChannel().queue(msg -> {
                msg.sendMessage(MessageUtils.getEmbed(Color.ORANGE)
                        .setDescription("Zahájil si proces přepojovaní discord učtu s MC účtem!\n" +
                                "Zadej na Lobby příkaz `/verify " + t.getCode() + "`.")
                        .setFooter("Tvůj kód expiruje za 15 minut.", null)
                        .build()).queue();
            });
            return;
        } else {
            ConnectTask s = Sussi.getInstance().getSql().getActiveConnectionTask(sender.getId());
            channel.sendMessage(MessageUtils.getEmbed(Color.RED)
                    .setDescription("Aktuálně máš neexpirovaný kód! Přeposílam kód do DM...")
            .build()).queue();
            sender.openPrivateChannel().queue(msg -> {
                msg.sendMessage(MessageUtils.getEmbed(Color.LIGHT_GRAY)
                        .setDescription("Momentálně máš neexpirovaný kód!\n"
                        + "Zadej na Lobby příkaz `/verify " + s.getCode() + "`.")
                        .setFooter("Tvůj kód expiruje za " + (TimeUnit.MILLISECONDS.toMinutes(s.getExpire() - System.currentTimeMillis())) + "min a " + (TimeUnit.MILLISECONDS.toSeconds(s.getExpire() - System.currentTimeMillis()) % 60) + "s.", null)
                        .build()).queue();
            });
            return;
        }
    }

    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    @Override
    public String getCommand() {
        return "verify";
    }

    @Override
    public String getDescription() {
        return "Přepojení discord profilu s MC účtem ve hře.";
    }

    @Override
    public String getHelp() {
        return ",verify";
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
