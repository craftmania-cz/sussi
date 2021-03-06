package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.Profile;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;


public class Link implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (Sussi.getInstance().getSql().isConnectedToMC(sender.getId())) {
            MessageUtils.sendErrorMessage("Tento účet již je propojen s MC účtem " + Sussi.getInstance().getSql().getMinecraftNick(sender.getId()) + "!", channel);
            return;
        }

        if (args.length < 1) {
            MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Př. `,link SUPERTAJNYKOD123`\nPro získaní kódu jdi na Lobby a použij `/link`", channel);
            return;
        }

        String code = args[0].toUpperCase();
        if (!Sussi.getInstance().getSql().doesConnectionExist(code)) {
            MessageUtils.sendErrorMessage("Tento kód nebyl nalezen v naší databázi!", channel);
            return;
        }

        String nick = Sussi.getInstance().getSql().getConnectionNick(code);
        channel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Účet byl úspěšně propojen").setDescription("Tento účet byl přepojen s MC nickem " + nick).build()).queue();
        Sussi.getInstance().getSql().connectToMC(sender.getId(), code);

        Profile profile = new Profile(nick);
        if (profile.hasAnyVIP()) Sussi.getVIPManager().getVIPProfiles().add(profile);

        Sussi.getVIPManager().checkMember(member.getGuild(), member);
    }


    @Override
    public String getCommand() {
        return "link";
    }

    @Override
    public String getDescription() {
        return "Propojení discord profilu s MC účtem ve hře.";
    }

    @Override
    public String getHelp() {
        return ",link <kód> - přepojení cez zadaný kód";
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
