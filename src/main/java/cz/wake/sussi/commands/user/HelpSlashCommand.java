package cz.wake.sussi.commands.user;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import static cz.wake.sussi.commands.user.Help.getContext;

public class HelpSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandEvent event) {

        // Variables
        MessageChannel textChannel = event.getChannel();

        // Command
        if(textChannel.getType() == ChannelType.TEXT){
            hook.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setTitle("Zkontroluj si zprávy")
                    .setDescription(":mailbox_with_mail: | Odeslala jsem ti do zpráv nápovědu s příkazy!").build()).queue();
        }
        event.getUser().openPrivateChannel().queue(msg -> {
            msg.sendMessage(MessageUtils.getEmbed(Constants.GREEN)
                    .setTitle("**Nápověda k Sussi**", null)
                    .setDescription(getContext())
                    .build()).queue();
        });
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Základní nápověda pro Sussi.";
    }

    @Override
    public String getHelp() {
        return "/help";
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
    public boolean isEphemeral() {
        return false;
    }
}
