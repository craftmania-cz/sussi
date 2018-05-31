package cz.wake.sussi.commands;

import cz.wake.sussi.commands.games.*;
import cz.wake.sussi.commands.mod.*;
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
        registerCommand(new Blacklist());
        registerCommand(new Hearthstone());
        registerCommand(new Fortnite());
        registerCommand(new ClashRoyale());
        registerCommand(new LeagueOfLegends());
        registerCommand(new GTA());
        registerCommand(new Osu());
        registerCommand(new Pubg());
        registerCommand(new RocketLeague());
        registerCommand(new Overwatch());
        registerCommand(new CsGO());
        registerCommand(new Factorio());
    }
}
