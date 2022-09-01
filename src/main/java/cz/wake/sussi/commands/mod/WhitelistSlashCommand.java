package cz.wake.sussi.commands.mod;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.WhitelistedIP;
import cz.wake.sussi.objects.WhitelistedNick;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;

public class WhitelistSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            return;
        }

        switch (subcommandName) {
            case "add" -> {
                String name = event.getOption("name").getAsString();
                String ipAddress = event.getOption("ipaddress").getAsString();

                for (WhitelistedNick nick : Sussi.getInstance().getSql().getWhitelistedNicks()) {
                    if (name.equals(nick.getNick())) {
                        hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.RED).setTitle("Nalezena shoda nicků").setDescription("Nick " + name + " se již na whitelistu nachází.").build()).queue();
                        return;
                    }
                }

                Sussi.getInstance().getSql().addWhitelistedNick(name, ipAddress);
                Sussi.getInstance().getSql().addWhitelistedIP(ipAddress, name);
                hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.GREEN).setDescription("Nick `" + name + "` byl úspěšně přidán na whitelist! Za 2-3 minuty se mu půjde připojit.").build()).queue();
            }
            case "remove" -> {
                if (event.getOption("name") != null) {
                    String name = event.getOption("name").getAsString();
                    Sussi.getInstance().getSql().removeWhitelistedName(name);
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.GREEN).setDescription("Nick `" + name + "` byl úspěšně odebrán na whitelistu.").build()).queue();
                }
                if (event.getOption("ipaddress") != null) {
                    String ipAddress = event.getOption("ipaddress").getAsString();
                    Sussi.getInstance().getSql().removeWhitelistedIP(ipAddress);
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.GREEN).setDescription("IP `" + ipAddress + "` byla úspěšně odebrán na whitelistu.").build()).queue();
                }
            }
            case "check" -> {
                if (event.getOption("ipaddress") != null) {
                    String ipAddress = event.getOption("ipaddress").getAsString();
                    for (WhitelistedIP ip : Sussi.getInstance().getSql().getWhitelistedIPs()) {
                        if (ipAddress.equals(ip.getAddress())) {
                            hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.yellow).setTitle("Informace o IP").setDescription("**" + ip.getAddress() + "** - " + ip.getDescription()).build()).queue();
                            return;
                        }
                    }
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.RED).setTitle("IP Nenalezena").setDescription("IP adresa " + ipAddress + " nebyla nalezena.").build()).queue();
                }
                if (event.getOption("name") != null) {
                    String name = event.getOption("name").getAsString();
                    for (WhitelistedNick nick : Sussi.getInstance().getSql().getWhitelistedNicks()) {
                        if (name.equals(nick.getNick())) {
                            hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.yellow).setTitle("Informace o nicku").setDescription("**" + nick.getNick() + "** - " + nick.getDescription()).build()).queue();
                            return;
                        }
                    }
                    hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.RED).setTitle("Nick nenalezen").setDescription("Nick " + name + " nebyl nalezen.").build()).queue();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "whitelist";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getHelp() {
        return "/whitelist add [nick] [ipAddress]";
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
    public boolean isEphemeral() {
        return false;
    }
}
