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

public class UnlinkSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, SlashCommandEvent event) {
        event.acknowledge(true).queue();
        CommandHook hook = event.getHook();
        hook.setEphemeral(true);

        User user = hook.getEvent().getUser();
        MessageChannel textChannel =  hook.getEvent().getChannel();

        // Command
        if (!Sussi.getInstance().getSql().isConnectedToMC(user.getId())) {
            MessageUtils.sendErrorMessage("Tento účet není propojen se žádnym MC účtem!", textChannel);
            return;
        }

        textChannel.sendMessage(MessageUtils.getEmbed(Color.GREEN).setTitle("Účet úspěšně odpojen").setDescription("Tvůj discord profil byl odpojen od MC účtu " + Sussi.getInstance().getSql().getMinecraftNick(user.getId()) + "!").build()).queue();
        Sussi.getInstance().getSql().disconnectFromMC(user.getId());
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
}
