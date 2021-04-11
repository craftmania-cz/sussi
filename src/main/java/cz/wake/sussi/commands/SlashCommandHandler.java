package cz.wake.sussi.commands;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.user.*;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.Command;
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

        commands.addCommands(
                new CommandUpdateAction.CommandData("standa", "Standův tajný příkaz, chceš čokošku?")
        );

        commands.addCommands(
                new CommandUpdateAction.CommandData("room", "Spravování vlastní hlasové místnosti")
                .addSubcommand(new CommandUpdateAction.SubcommandData("help", "Zobrazení nápovědy pro spravování voice kanálu"))
                .addSubcommand(new CommandUpdateAction.SubcommandData("lock", "Uzamknutí voice místnosti"))
                .addSubcommand(new CommandUpdateAction.SubcommandData("unlock", "Odemknutí voice místnosti"))
                .addSubcommand(new CommandUpdateAction.SubcommandData("add", "Přidání práv pro uživatele do uzamknuté místnosti")
                        .addOption(new CommandUpdateAction.OptionData(USER, "user", "Uživatel, kterému budou dány práva do voice").setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("remove", "Odebrání práv pro uživatele z místnosti")
                        .addOption(new CommandUpdateAction.OptionData(USER, "user", "Uživatel, kterému budou odebrány práva do voice").setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("name", "Změnit název voice místnosti")
                        .addOption(new CommandUpdateAction.OptionData(STRING, "text", "Nový název místnosti").setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("bitrate", "Nastavení kvality hovoru v místnosti.")
                        .addOption(new CommandUpdateAction.OptionData(INTEGER, "value", "Hodnota kbps, která se nastaví pro místnost.").setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("unlimited", "Nastaví neomezený počet připojení do místnosti (limit 99)."))
                .addSubcommand(new CommandUpdateAction.SubcommandData("limit", "Nastaví limit uživatelů v místnosti.")
                        .addOption(new CommandUpdateAction.OptionData(INTEGER, "value", "Limit uživatelů, který se nastaví pro místnost.").setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("ban", "Zablokování a skryté voice místnosti pro uživatele")
                        .addOption(new CommandUpdateAction.OptionData(USER, "user", "Uživatel, který bude zabanován z místnosti.").setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("unban", "Zrušení blokace přístupu uživateli do voice místnosti.")
                        .addOption(new CommandUpdateAction.OptionData(USER, "user", "Uživatel, který bude mít opět přístup do místnosti.").setRequired(true)))
                .addSubcommand(new CommandUpdateAction.SubcommandData("kick", "Vyhození uživatele z místnosti")
                        .addOption(new CommandUpdateAction.OptionData(USER, "user","Uživatel, který bude vyhozen z místnosti").setRequired(true)))
        );

        commands.addCommands(
                new CommandUpdateAction.CommandData("wiki", "Rychlé odkazy na témata a návody na naší Wiki")
                .addOption(new CommandUpdateAction.OptionData(STRING, "id", "Výběr tématu nebo návodu z Wiki").setRequired(true)
                    .addChoice("Pravidla serveru", "pravidla")
                    .addChoice("Povolené a zakázané módy", "povolene-mody")
                    .addChoice("Jak se připojit na server", "jak-se-pripojit")
                    .addChoice("Problémy s resource packem", "problemy-s-resource-packem")
                    .addChoice("Návod: Jak na Craftmania Discord", "navod-discord")
                    .addChoice("Návod: Jak najít screeny, logy atd.", "navod-screeny-a-logy"))
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

        // General příkazy
        registerSlashCommand(new HelpSlashCommand());
        registerSlashCommand(new LinkSlashCommand());
        registerSlashCommand(new UnlinkSlashCommand());
        registerSlashCommand(new RoomSlashCommand());
        registerSlashCommand(new WikiSlashCommand());

        // Fun příkazy
        registerSlashCommand(new StandaSlashCommand());
    }






}
