package cz.wake.sussi.commands.slash;

import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import static cz.wake.sussi.commands.user.Help.getContext;

public class HelpSlashCommand {

    public void call(SlashCommandEvent event) {
        event.acknowledge(true).queue();
        CommandHook hook = event.getHook();
        hook.setEphemeral(true);

        // Variables
        MessageChannel textChannel =  hook.getEvent().getChannel();

        // Command
        if(textChannel.getType() == ChannelType.TEXT){
            textChannel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Zkontroluj si zprávy")
                    .setDescription(":mailbox_with_mail: | Odeslala jsem ti do zpráv nápovědu s příkazy!").build()).queue();
        }
        event.getUser().openPrivateChannel().queue(msg -> {
            msg.sendMessage(MessageUtils.getEmbed(Constants.GREEN)
                    .setTitle("**Nápověda k Sussi**", null)
                    .setDescription(getContext())
                    .build()).queue();
        });
    }
}
