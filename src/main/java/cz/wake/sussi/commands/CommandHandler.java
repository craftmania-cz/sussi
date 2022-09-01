package cz.wake.sussi.commands;

import cz.wake.sussi.commands.mod.*;
import cz.wake.sussi.commands.user.*;
import cz.wake.sussi.utils.SussiLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {

    public static List<ICommand> commands = new ArrayList<>();

    public void registerCommand(ICommand c) {
        try {
            commands.add(c);
        } catch (Exception e) {
            SussiLogger.warnMessage("Error during register command - " + c.getCommand() + " .");
        }
    }

    public void unregisterCommand(ICommand c) {
        commands.remove(c);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    public List<ICommand> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    public void register() {
        SussiLogger.infoMessage("Loading all commands.");
        registerCommand(new Help());
        registerCommand(new Blacklist());
        registerCommand(new BlockCountry());
        registerCommand(new Napad());
        registerCommand(new BugPoints());
        registerCommand(new CraftBox());
        SussiLogger.greatMessage("Sussi will respond to (" + commands.size() + ") commands.");
    }
}
