package cz.wake.sussi.commands.console.commands;

import cz.wake.sussi.commands.console.commands.generic.AbstractConsoleCommand;

public class StopConsoleCommand extends AbstractConsoleCommand {

    public StopConsoleCommand() {
        this.name = "stop";
    }

    @Override
    public void execute(String arguments) {
        System.exit(0);
    }
}
