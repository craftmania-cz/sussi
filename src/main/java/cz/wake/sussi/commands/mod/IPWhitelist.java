package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.WhitelistedIP;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;

public class IPWhitelist implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(sender.getId().equals("238410025813540865") || sender.getId().equals("177516608778928129")) {
            List<WhitelistedIP> ips = Sussi.getInstance().getSql().getWhitelistedIPs();

            if(args.length < 1) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Seznam IP adres na whitelistu");
                builder.setColor(Color.yellow);
                for(WhitelistedIP ip : ips) {
                    builder.appendDescription("**" + ip.getAddress() + "** - " + ip.getDescription() + "\n");
                }
                channel.sendMessage(builder.build()).queue();
                return;
            }

            if(args[0].equals("add")) {
                if(args.length < 3) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Nesprávně napsané argumenty! Př. ,ipwl add 1.1.1.1 Testovaci Zprava").build()).queue();
                    return;
                }

                for(WhitelistedIP ip : Sussi.getInstance().getSql().getWhitelistedIPs()) {
                    if(args[1].equals(ip.getAddress())) {
                        channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("IP Nalezena").setDescription("IP adresa " + args[1] + " již existuje.").build()).queue();
                        return;
                    }
                }

                String description = args[2];

                for(int i = 3; i != args.length; i++) {
                    description += " " + args[i];
                }

                Sussi.getInstance().getSql().addWhitelistedIP(args[1], description);
                channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Příkaz byl úspěsně vykonán").setDescription("IP Adresa " + args[1] + " byla úspěšně přidána!").build()).queue();
            }

            if(args[0].equals("remove")) {
                if(args.length < 2) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Nesprávně napsané argumenty! Př. ,ipwl remove 1.1.1.1").build()).queue();
                    return;
                }

                for(WhitelistedIP ip : Sussi.getInstance().getSql().getWhitelistedIPs()) {
                    if(args[1].equals(ip.getAddress())) {
                        Sussi.getInstance().getSql().removeWhitelistedIP(args[1]);
                        channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Příkaz byl úspěsně vykonán").setDescription("IP Adresa " + args[1] + " byla úspěšně odebrána!").build()).queue();
                        return;
                    }
                }
                channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("IP Nenalezena").setDescription("IP adresa " + args[1] + " nebyla nalezena.").build()).queue();
            }

            if(args[0].equals("check")) {
                if(args.length < 2) {
                    channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazů").setDescription("Nesprávně napsané argumenty! Př. ,ipwl check 1.1.1.1").build()).queue();
                    return;
                }

                for(WhitelistedIP ip : Sussi.getInstance().getSql().getWhitelistedIPs()) {
                    if(args[1].equals(ip.getAddress())) {
                        channel.sendMessage(MessageUtils.getEmbed(Color.yellow).setTitle("Informace o IP").setDescription("**" + ip.getAddress() + "** - " + ip.getDescription()).build()).queue();
                        return;
                    }
                }
                channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("IP Nenalezena").setDescription("IP adresa " + args[1] + " nebyla nalezena.").build()).queue();
                return;
            }
        } else {
            channel.sendMessage(MessageUtils.getEmbed(Color.RED).setTitle("Chyba při vykonávání příkazu").setDescription("Na toto mají právo pouze Krosta a Kwak!").build()).queue();
        }
    }

    @Override
        public String getCommand() {
        return "ipwl";
    }

    @Override
    public String getDescription() {
        return "Sprava IPWhitelistu na serveru";
    }

    @Override
    public String getHelp() {
        return ",ipwl - Zobrazí seznam IP adres na whitelistu\n" +
                ",ipwl add <ip> <description> - Přidá IP adresu na whitelist\n" +
                ",ipwl remove <ip> - Odebere IP adresu z whitelistu\n" +
                ",ipwl check <ip> - Zkontroluje zda je IP na whitelistu";
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
