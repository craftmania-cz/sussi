package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class LinkSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, CommandHook hook, SlashCommandEvent event) {

        SlashCommandEvent.OptionData optionKeyId = event.getOption("key");
        String keyId = optionKeyId.getAsString();

        if (keyId != null) {
            if (!Sussi.getInstance().getSql().doesConnectionExist(keyId)) {
                hook.sendMessage(MessageUtils.getEmbedError().setDescription("Tento kód nebyl nalezen v naší databázi!").build()).queue();
                return;
            }

            hook.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Účet byl úspěšně propojen").setDescription("Tento účet byl přepojen s MC nickem " + Sussi.getInstance().getSql().getConnectionNick(keyId)).build()).queue();
            Sussi.getInstance().getSql().connectToMC(sender.getId(), keyId);
            Sussi.getVIPManager().checkMember(member.getGuild(), member);
        } else {
            hook.sendMessage(MessageUtils.getEmbedError().setDescription("Špatně zadaný příkaz! Př. `/link SUPERTAJNYKOD123`\nPro získaní kódu jdi na Lobby a použij `/link`").build()).queue();
        }
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getDescription() {
        return "Propojení discord profilu s MC účtem ve hře.";
    }

    @Override
    public String getHelp() {
        return "/link key:<kód> - Propojení skrz kod z lobby";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
