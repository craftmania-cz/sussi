package cz.wake.sussi.commands;

import cz.wake.sussi.Sussi;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;

import static net.dv8tion.jda.api.entities.Command.OptionType.*;

public class SlashCommandHandler {

    private CommandUpdateAction commands;

    public SlashCommandHandler() {
        this.commands = Sussi.getJda().updateCommands();
    }

    public void createAndUpdateCommands() {

        commands.addCommands(
                new CommandUpdateAction.CommandData("help", "Nápověda k Sussi")
        );

        commands.addCommands(
                new CommandUpdateAction.CommandData("unlink", "Odpojení svého CraftMania profilu od tvého Discord účtu.")
        );

        commands.addCommands(
                new CommandUpdateAction.CommandData("link", "Propojení CraftMania účtu s tvým Discord účtem.")
                    .addOption(new CommandUpdateAction.OptionData(STRING,"linkId", "Klíč, který se ti zobrazit skrz /link příkaz na serveru").setRequired(true))
        );

        // Finální update všech slash příkazů
        commands.queue();
    }






}
