package cz.wake.sussi.commands;

import cz.wake.sussi.commands.games.*;
import cz.wake.sussi.commands.mod.*;
import cz.wake.sussi.commands.owner.Stop;
import cz.wake.sussi.commands.owner.Udrzba;
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
        registerCommand(new Ats());
        registerCommand(new Help());
        registerCommand(new Cmarchiv());
        registerCommand(new Stop());
        registerCommand(new Pravidla());
        registerCommand(new GGT());
        registerCommand(new CheckIP());
        registerCommand(new Unban());
        registerCommand(new CheckBan());
        registerCommand(new Blacklist());
        registerCommand(new Fortnite());
        registerCommand(new ClashRoyale());
        registerCommand(new LeagueOfLegends());
        registerCommand(new GTA());
        registerCommand(new Osu());
        registerCommand(new RocketLeague());
        registerCommand(new Overwatch());
        registerCommand(new Dotaznik());
        registerCommand(new Standa());
        registerCommand(new Udrzba());
        registerCommand(new IPWhitelist());
        registerCommand(new Hytale());
        registerCommand(new Events());
        registerCommand(new Anime());
        registerCommand(new Apex());
        registerCommand(new Paladins());
        SussiLogger.greatMessage("Sussi will respond to (" + commands.size() + ") commands.");
    }
}
