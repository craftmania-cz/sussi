package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;

public class UnlinkSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {

        User user = event.getUser();

        // Command
        if (!Sussi.getInstance().getSql().isConnectedToMC(user.getId())) {
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Tento účet není propojen se žádnym MC účtem!").build()).queue();
            return;
        }

        hook.sendMessageEmbeds(MessageUtils.getEmbed(Color.GREEN).setTitle("Účet úspěšně odpojen").setDescription("Tvůj discord profil byl odpojen od MC účtu " + Sussi.getInstance().getSql().getMinecraftNick(user.getId()) + "!").build()).queue();
        Sussi.getInstance().getSql().disconnectFromMC(user.getId());
        Sussi.getInstance().getSql().updateBooster(user.getId(), 0);

        Role role = event.getGuild().getRoleById("876294038985265212"); // Verified role
        event.getGuild().removeRoleFromMember(member, role).queue();
    }

    @Override
    public String getName() {
        return "unlink";
    }

    @Override
    public String getDescription() {
        return "Odpojení discord profilu z MC účtu.";
    }

    @Override
    public String getHelp() {
        return "/unlink";
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
    public boolean defferReply() {
        return true;
    }
}
