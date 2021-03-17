package cz.wake.sussi.commands.slash;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class LinkSlashCommand {

    public void call(SlashCommandEvent event) {
        event.acknowledge(true).queue();
        CommandHook hook = event.getHook();
        hook.setEphemeral(true);

        User user = hook.getEvent().getUser();
        Member member = hook.getEvent().getMember();
        MessageChannel textChannel =  hook.getEvent().getChannel();
        SlashCommandEvent.OptionData optionKeyId = event.getOption("key");
        String keyId = optionKeyId.getAsString();

        if (keyId != null) {
            if (!Sussi.getInstance().getSql().doesConnectionExist(keyId)) {
                MessageUtils.sendErrorMessage("Tento kód nebyl nalezen v naší databázi!", textChannel);
                return;
            }

            textChannel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Účet byl úspěšně propojen").setDescription("Tento účet byl přepojen s MC nickem " + Sussi.getInstance().getSql().getConnectionNick(keyId)).build()).queue();
            Sussi.getInstance().getSql().connectToMC(user.getId(), keyId);
            Sussi.getVIPManager().checkMember(member.getGuild(), member);
        } else {
            MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Př. `/link SUPERTAJNYKOD123`\nPro získaní kódu jdi na Lobby a použij `/link`", textChannel);
        }

    }
}
