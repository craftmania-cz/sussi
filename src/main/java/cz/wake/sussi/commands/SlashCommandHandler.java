package cz.wake.sussi.commands;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.slash.*;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.entities.Command.OptionType.*;

public class SlashCommandHandler {

    private CommandUpdateAction commands;
    public static List<ISlashCommand> list = new ArrayList<>();

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
                    .addOption(new CommandUpdateAction.OptionData(STRING,"key", "Klíč, který se ti zobrazit skrz /link příkaz na serveru").setRequired(true))
        );

        // Finální update všech slash příkazů
        commands.queue();
        this.registerSlashCommands();
    }

    public void registerSlashCommand(ISlashCommand command) {
        try {
            list.add(command);
        } catch (Exception e) {
            SussiLogger.warnMessage("Error during register command - " + command.getName() + " .");
        }
    }

    public List<ISlashCommand> getSlashCommands() {
        return list;
    }

    public List<ISlashCommand> getCommandsByType(CommandType type) {
        return list.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    private void registerSlashCommands() {
        registerSlashCommand(new HelpSlashCommand());
        registerSlashCommand(new LinkSlashCommand());
        registerSlashCommand(new UnlinkSlashCommand());
    }






}
