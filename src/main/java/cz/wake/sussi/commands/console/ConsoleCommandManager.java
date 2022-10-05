package cz.wake.sussi.commands.console;

import cz.wake.sussi.commands.console.commands.HelpConsoleCommand;
import cz.wake.sussi.commands.console.commands.StopConsoleCommand;
import cz.wake.sussi.commands.console.commands.generic.AbstractConsoleCommand;
import cz.wake.sussi.utils.SussiLogger;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ConsoleCommandManager {

    // Data
    private static @Getter final List<AbstractConsoleCommand> consoleCommands = new ArrayList<>();

    // Runtime
    private static @Getter Thread commandThread;

    public static void init() {
        consoleCommands.add(new HelpConsoleCommand());
        consoleCommands.add(new StopConsoleCommand());

        startCommandThread();
    }

    public static void registerCommand(AbstractConsoleCommand consoleCommand) {
        consoleCommands.add(consoleCommand);
    }

    private static void processCommand(String command) {
        if (command == null) {
            return;
        }

        /*ArgumentParser argumentParser = new ArgumentParser(command);

        if (!argumentParser.hasAnyArguments()) {
            SussiLogger.dangerMessage("Unknown command '" + command + "'!");
            return;
        }

        String name = argumentParser.getArgumentAtIndex(0).getValue();
        String arguments = "";

        if (argumentParser.hasArgumentAtIndex(1)) {
            arguments = argumentParser.getAllArgumentsAfterIndex(1).getValue();
        }

        for (AbstractConsoleCommand abstractConsoleCommand : consoleCommands) {
            if (abstractConsoleCommand.name.equalsIgnoreCase(name)) {
                try {
                    abstractConsoleCommand.execute(arguments);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    SussiLogger.dangerMessage("Exception occurred while executing command '" + command + "'!");
                }
                return;
            }
        }*/

        SussiLogger.dangerMessage("Unknown command '" + command + "'!");
    }

    private static void startCommandThread() {
        commandThread = new Thread(() -> {
            while (true) {
                String command = System.console().readLine();
                //processCommand(command);
            }
        });
        commandThread.start();
    }
}
