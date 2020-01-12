package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NickWordBlacklist implements ICommand {

    private Paginator.Builder pBuilder = new Paginator.Builder()
            .setColumns(1)
            .setItemsPerPage(10)
            .showPageNumbers(true)
            .waitOnSinglePage(false)
            .useNumberedItems(true)
            .setFinalAction(m -> {
                try {
                    m.clearReactions().queue();
                } catch (PermissionException e) {
                    m.delete().queue();
                }
            })
            .setTimeout(1, TimeUnit.MINUTES);

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        pBuilder.setEventWaiter(w);
        if(sender.getId().equals("177516608778928129")) {
            if(args.length < 1) {
                channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - NickWordBlacklist :question:")
                        .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
                return;
            }
            List<String> words = Sussi.getInstance().getSql().getBlacklistedNameWords();
            List<String> nicks = Sussi.getInstance().getSql().getAllowedBlacklistedNames();

            if(args[0].equals("blacklist")) {
                if(args.length < 2) {
                    if(words.isEmpty()) {
                        MessageUtils.sendErrorMessage("No, žádné slovo ve jméně není zablokované!", channel);
                        return;
                    }
                    for(String word : words) {
                        pBuilder.addItems("**" + word + "**");
                    }
		    pBuilder.setText("Zde je seznam nepovolených slov ve jméně").build().paginate(channel, 1);
                    return;
                }

                if(args.length > 1) {
                    if(args[1].equals("add")) {
                        if(args.length < 3) {
                            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - NickWordBlacklist :question:")
                                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
                            return;
                        }
                        String word = args[2].toLowerCase();
                        if(words.contains(word)) {
                            channel.sendMessage(MessageUtils.getEmbed().setDescription("Toto slovo je již zakázané!").setColor(Color.RED).build()).queue();
                            return;
                        }
                        Sussi.getInstance().getSql().addBlacklistedNameWord(word);
                        channel.sendMessage(MessageUtils.getEmbed().setDescription("Slovo " + args[2] + " bylo úspěšně zablokované!").setColor(Color.GREEN).build()).queue();
                        return;
                    }
                    if(args[1].equals("remove")) {
                        if(args.length < 3) {
                            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - NickWordBlacklist :question:")
                                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
                            return;
                        }
                        String word = args[2].toLowerCase();
                        if(!words.contains(word)) {
                            channel.sendMessage(MessageUtils.getEmbed().setDescription("Toto slovo není zakázané!").setColor(Color.RED).build()).queue();
                            return;
                        }
                        Sussi.getInstance().getSql().removeBlacklistedNameWord(word);
                        channel.sendMessage(MessageUtils.getEmbed().setDescription("Slovo " + args[2] + " bylo úspěšně odblokované!").setColor(Color.GREEN).build()).queue();
                        return;
                    } else {
                        if(words.contains(args[1].toLowerCase())) {
                            channel.sendMessage(MessageUtils.getEmbed().setDescription("Slovo " + args[1] + " je zakázané!").setColor(Color.YELLOW).build()).queue();
                            return;
                        }
                        channel.sendMessage(MessageUtils.getEmbed().setDescription("Slovo " + args[1] + " není zakázané!").setColor(Color.YELLOW).build()).queue();
                        return;
                    }
                }
            }

            if(args[0].equals("whitelist")) {
                if(args.length < 2) {
                    if(nicks.isEmpty()) {
                        MessageUtils.sendErrorMessage("No, žádné jméno se zablokovaným slovem není povolené!", channel);
                        return;
                    }

                    for(String nick : nicks) {
                        String blacklisted = containedWord(words, nick);
                        pBuilder.addItems("**" + nick + "** - " + (blacklisted.equals("") ? "Neobsahuje zablokované slovo" : ("Obsahuje slovo: " + blacklisted)));
                    }
		    pBuilder.setText("Zde je seznam jmen, které jsou povolené i přes blacklist").build().paginate(channel, 1);
                    return;
                }
                if(args.length > 1) {
                    if(args[1].equals("add")) {
                        if(args.length < 3) {
                            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - NickWordBlacklist :question:")
                                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
                            return;
                        }
                        String nick = args[2].toLowerCase();
                        if(nicks.contains(nick)) {
                            channel.sendMessage(MessageUtils.getEmbed().setDescription("Toto jméno je již povolené!").setColor(Color.RED).build()).queue();
                            return;
                        }
                        Sussi.getInstance().getSql().addAllowedBlacklistedName(nick);
                        channel.sendMessage(MessageUtils.getEmbed().setDescription("Jméno " + args[2] + " bylo úspěšně povolené!").setColor(Color.GREEN).build()).queue();
                        return;
                    }
                    if(args[1].equals("remove")) {
                        if(args.length < 3) {
                            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - NickWordBlacklist :question:")
                                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
                            return;
                        }
                        String nick = args[2].toLowerCase();
                        if(!nicks.contains(nick)) {
                            channel.sendMessage(MessageUtils.getEmbed().setDescription("Toto jméno není povolené!").setColor(Color.RED).build()).queue();
                            return;
                        }
                        Sussi.getInstance().getSql().removeAllowedBlacklistedName(nick);
                        channel.sendMessage(MessageUtils.getEmbed().setDescription("Jméno " + args[2] + " bylo úspěšně zablokované!").setColor(Color.GREEN).build()).queue();
                        return;
                    } else {
                        if(nicks.contains(args[1].toLowerCase())) {
                            channel.sendMessage(MessageUtils.getEmbed().setDescription("Jméno " + args[1] + " je povolené, i když obsahuje zakázané slovo " + containedWord(words, args[1]) + "!").setColor(Color.YELLOW).build()).queue();
                            return;
                        }
                        channel.sendMessage(MessageUtils.getEmbed().setDescription("Jméno " + args[1] + " není povolené, protože obsahuje zakázané slovo " + containedWord(words, args[1]) + "!").setColor(Color.YELLOW).build()).queue();
                        return;
                    }
                }
            } else {
                channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - NickWordBlacklist :question:")
                        .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
                return;
            }
        } else {
            MessageUtils.sendErrorMessage("Na toto má práva pouze Kwak!", channel);
            return;
        }
    }

    @Override
    public String getCommand() {
        return "nickbl";
    }

    @Override
    public String getDescription() {
        return "Sprava blokace slov v nicku na serveru.";
    }

    @Override
    public String getHelp() {
        return ",nickbl blacklist - Zobrazí zablokované slova ve jméně\n" +
                ",nickbl blacklist <word> - Zobrazí, zda je slovo zablokované ve jméně\n" +
                ",nickbl blacklist add <word> - Zakáže slovo ve jméně\n" +
                ",nickbl blacklist remove <word> - Povolí slovo ve jméně\n" +
                ",nickbl whitelist - Zobrazí povolená jména + jejich nepovolené slovo\n" +
                ",nickbl whitelist <nick> - Zobrazí, zda je slovo povolené a jaké má nepovolené slovo\n" +
                ",nickbl whitelist add <nick> - Povolí jméno s nepovoleným slovem\n" +
                ",nickbl whitelist remove <nick> - Zakáže jméno s nepovoleným slovem";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    private String containedWord(List<String> blacklist, String nick) {
        for(String blacklisted : blacklist) {
            if(nick.toLowerCase().contains(blacklisted.toLowerCase())) {
                return blacklisted;
            }
        }
        return "";
    }
}
