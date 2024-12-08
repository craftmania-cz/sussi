package cz.wake.sussi.listeners;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null || event.getUser().isBot()) {
            return;
        }
        for (ISlashCommand slashCommand : Sussi.getSlashCommandHandler().getSlashCommands()) {
            if (slashCommand.getName().equals(event.getName())) {
                if (Rank.getPermLevelForUser(event.getUser(), (TextChannel) event.getChannel()).isAtLeast(slashCommand.getRank())) {
                    if (slashCommand.defferReply()) { // Fix -> true musí být pokud se používají mayu hooky, opačně nefungují všechny ostaní příkazy
                        event.deferReply(slashCommand.isEphemeral()).queue();
                    }
                    try {
                        slashCommand.onSlashCommand(event.getUser(), event.getChannel(), event.getMember(), event.getHook(), event);
                        SussiLogger.commandMessage("Type: " + event.getName() + " - Channel: " + event.getChannel() + " - User: " + event.getUser().getAsTag());
                    } catch (Exception exception) {
                        SussiLogger.fatalMessage("Internal error when executing the command!");
                        MessageUtils.sendErrorMessage(MessageUtils.getEmbedError()
                                .setDescription("Nastala chyba při provádění příkazu. Zkus to prosím později.")
                                .addField("Chyba příkazu", "`" + exception + "`", false), event.getHook());
                        exception.printStackTrace();
                    }
                } else {
                    event.deferReply(true).queue(success -> {
                        success.editOriginal("Na tento příkaz nemáš dostatečná práva.").queue();
                    });
                }
            }
        }
    }
}
