package cz.wake.sussi.commands.console.commands;

import cz.wake.sussi.commands.console.ConsoleCommandManager;
import cz.wake.sussi.commands.console.commands.generic.AbstractConsoleCommand;
import cz.wake.sussi.utils.SussiLogger;

public class HelpConsoleCommand extends AbstractConsoleCommand {

    public HelpConsoleCommand() {
        this.name = "help";
    }

    @Override
    public void execute(String arguments) {
        SussiLogger.infoMessage("=== Loaded Commands (" + ConsoleCommandManager.getConsoleCommands().size() + ") ===");
        for (var consoleCommand : ConsoleCommandManager.getConsoleCommands()) {
            SussiLogger.infoMessage("> " + consoleCommand.name);
        }
    }
}
