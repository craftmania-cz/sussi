package cz.wake.sussi.commands;

import cz.wake.sussi.commands.mod.Ats;
import cz.wake.sussi.commands.mod.CheckBan;
import cz.wake.sussi.commands.mod.CheckIP;
import cz.wake.sussi.commands.mod.GGT;
import cz.wake.sussi.commands.owner.Stop;
import cz.wake.sussi.commands.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {

    public static List<ICommand> commands = new ArrayList<>();

    public void registerCommand(ICommand c) {
        try {
            commands.add(c);
        } catch (Exception e) {
            //
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
        registerCommand(new Ats());
        registerCommand(new Help());
        registerCommand(new Cmarchiv());
        registerCommand(new Stop());
        registerCommand(new NasranoVKytare());
        registerCommand(new Pravidla());
        registerCommand(new GGT());
        registerCommand(new CheckIP());
        registerCommand(new Unban());
        registerCommand(new Liturkey());
        registerCommand(new CheckBan());
    }
}
