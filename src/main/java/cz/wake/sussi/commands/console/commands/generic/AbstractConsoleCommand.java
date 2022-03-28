package cz.wake.sussi.commands.console.commands.generic;

public abstract class AbstractConsoleCommand {

    public String name;

    public abstract void execute(String arguments);

}
