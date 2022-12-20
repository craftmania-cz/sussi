package cz.wake.sussi.commands;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.mod.*;
import cz.wake.sussi.commands.owner.SetupCommand;
import cz.wake.sussi.commands.user.*;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SlashCommandHandler {

    private CommandListUpdateAction commands;
    public static List<ISlashCommand> list = new ArrayList<>();

    public SlashCommandHandler() {
        this.commands = Sussi.getJda().updateCommands();
    }

    public void createAndUpdateCommands() {

        commands.addCommands(Commands.slash("help", "Nápověda k Sussi"));
        commands.addCommands(Commands.slash("unlink", "Odpojení svého CraftMania profilu od tvého Discord účtu."));

        commands.addCommands(Commands.slash("link", "Propojení CraftMania účtu s tvým Discord účtem.")
                .addOptions(new OptionData(OptionType.STRING, "key", "Klíč, který se ti zobrazit skrz /link příkaz na serveru").setRequired(true)));

        commands.addCommands(Commands.slash("room", "Spravování vlastní hlasové místnosti")
                .addSubcommands(new SubcommandData("info", "Zobrazení aktuální nastavení voice kanálu"))
                .addSubcommands(new SubcommandData("help", "Zobrazení nápovědy pro spravování voice kanálu"))
                .addSubcommands(new SubcommandData("lock", "Uzamknutí voice místnosti"))
                .addSubcommands(new SubcommandData("unlock", "Odemknutí voice místnosti"))
                .addSubcommands(new SubcommandData("add", "Přidání práv pro uživatele do uzamknuté místnosti")
                    .addOptions(new OptionData(OptionType.USER, "user", "Uživatel, kterému budou dány práva do voice").setRequired(true)))
                .addSubcommands(new SubcommandData("remove", "Odebrání práv pro uživatele z místnosti")
                    .addOptions(new OptionData(OptionType.USER, "user", "Uživatel, kterému budou odebrány práva do voice").setRequired(true)))
                .addSubcommands(new SubcommandData("name", "Změnit název voice místnosti")
                    .addOptions(new OptionData(OptionType.STRING, "text", "Nový název místnosti").setRequired(true)))
                .addSubcommands(new SubcommandData("bitrate", "Nastavení kvality hovoru v místnosti.")
                    .addOptions(new OptionData(OptionType.INTEGER, "value", "Hodnota kbps, která se nastaví pro místnost.").setRequired(true)))
                .addSubcommands(new SubcommandData("unlimited", "Nastaví neomezený počet připojení do místnosti (limit 99)."))
                .addSubcommands(new SubcommandData("limit", "Nastaví limit uživatelů v místnosti.")
                    .addOptions(new OptionData(OptionType.INTEGER, "value", "Limit uživatelů, který se nastaví pro místnost.").setRequired(true)))
                .addSubcommands(new SubcommandData("ban", "Zablokování a skryté voice místnosti pro uživatele")
                    .addOptions(new OptionData(OptionType.USER, "user", "Uživatel, který bude zabanován z místnosti.").setRequired(true)))
                .addSubcommands(new SubcommandData("unban", "Zrušení blokace přístupu uživateli do voice místnosti.")
                    .addOptions(new OptionData(OptionType.USER, "user", "Uživatel, který bude mít opět přístup do místnosti.").setRequired(true)))
                .addSubcommands(new SubcommandData("kick", "Vyhození uživatele z místnosti")
                    .addOptions(new OptionData(OptionType.USER, "user","Uživatel, který bude vyhozen z místnosti").setRequired(true))));

        commands.addCommands(Commands.slash("wiki", "Rychlé odkazy na témata a návody na naší Wiki")
                .addOptions(new OptionData(OptionType.STRING, "id", "Výběr tématu nebo návodu z Wiki").setRequired(true)
                    .addChoice("Pravidla serveru", "pravidla")
                    .addChoice("Povolené a zakázané módy", "povolene-mody")
                    .addChoice("Jak se připojit na server", "jak-se-pripojit")
                    .addChoice("Problémy s resource packem", "problemy-s-resource-packem")
                    .addChoice("Návod: Jak na Craftmania Discord", "navod-discord")
                    .addChoice("Návod: Jak najít screeny, logy", "navod-screeny-a-logy")));

        commands.addCommands(Commands.slash("choose", "Vyber nějaké možnosti a nech na Sussi ať vybere tu správnou.")
                .addOptions(new OptionData(OptionType.STRING, "opt1", "První hodnota na zvolení").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "opt2", "Druhá hodnota na zvolení").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "opt3", "Třetí hodnota na zvolení"))
                .addOptions(new OptionData(OptionType.STRING, "opt4", "Čtvrtá hodnota na zvolení"))
                .addOptions(new OptionData(OptionType.STRING, "opt5", "Pátá hodnota na zvolení"))
                .addOptions(new OptionData(OptionType.STRING, "opt6", "Šestí hodnota na zvolení")));

        commands.addCommands(Commands.slash("checkip", "Kontrola IP adresy, zda je VPN, z jakého je kraje a jiné informace.")
                .addOptions(new OptionData(OptionType.STRING, "ip", "IP adresa ve formát IPv4 nebo IPv6"))
                .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče, který bude zkontrolován")));

        commands.addCommands(Commands.slash("uuid", "Získání online a offline UUID podle nicku hráče.")
                .addOptions(new OptionData(OptionType.STRING, "name", "Minecraft nick hráče").setRequired(true)));

        commands.addCommands(Commands.slash("profile", "Zobrazení CraftMania statistik a hráčského profilu.")
                .addOptions(new OptionData(OptionType.STRING, "name", "Minecraft nick hráče z serveru"))
                .addOptions(new OptionData(OptionType.USER, "user", "Discord uživatel, který má propojený účet.")));

        commands.addCommands(Commands.slash("profile-settings", "Nastavení svého profilu pro statistiky na serveru.")
                .addSubcommands(new SubcommandData("status", "Nastavení statusu pro svůj profil na serveru")
                    .addOptions(new OptionData(OptionType.STRING, "text", "Status, který se nastaví na tvůj profil.").setRequired(true)))
                .addSubcommands(new SubcommandData("gender", "Nastavení pohlaví pro svůj profil")
                    .addOptions(new OptionData(OptionType.STRING, "type", "Zvolení pohlaví, co se nastaví na profil").setRequired(true)
                        .addChoice("Muž", "1")
                        .addChoice("Žena", "2")
                        .addChoice("Nezvoleno", "0"))));

        commands.addCommands(Commands.slash("checkban", "Zkontrolování zda má hráč aktivní ban a odkoho.")
                //.addOptions(new OptionData(OptionType.STRING, "ip", "IP adresa ve formát IPv4 nebo IPv6"))
                .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče, který bude zkontrolován").setRequired(true)));

        commands.addCommands(Commands.slash("stafflist", "Seznam členů AT a jejich ID pro přidání do ticketů."));

        commands.addCommands(Commands.slash("ats", "Zobrazení statistik o tom, jak který člen AT je aktivní na serveru.")
                        .addOptions(new OptionData(OptionType.STRING, "name", "Jméno člena AT k zobrazení.", false))
                        .addOptions(new OptionData(OptionType.USER, "user", "Uživatel z AT k zobrazení", false)));
                        
        commands.addCommands(Commands.slash("booster", "Nastavění vlastní booster barvy.")
                .addOptions(new OptionData(OptionType.STRING, "hex_color", "Barva ve formátu hex.").setRequired(true)));

        commands.addCommands(Commands.slash("setup", "Nastavení specifických nastavení na Discord serveru")
                .addOptions(new OptionData(OptionType.STRING, "type", "Hodnota k nastavení").setRequired(true)
                        .addChoice("Výběr rolí", "role-selector")));

        commands.addCommands(Commands.slash("whitelist", "Správa whitelistu serveru, pokud se připojuje na server mimo CZ/SK.")
                .addSubcommands(new SubcommandData("add", "Přidání hráče na whitelist")
                    .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče").setRequired(true))
                    .addOptions(new OptionData(OptionType.STRING, "ipaddress", "IP adresa hráče, lze použít IPv4 i IPv6").setRequired(true)))
                .addSubcommands(new SubcommandData("remove", "Odebrání hráče z whitelistu")
                    .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče"))
                    .addOptions(new OptionData(OptionType.STRING, "ipaddress", "IP adresa hráče, lze použít IPv4 i IPv6")))
                .addSubcommands(new SubcommandData("check", "Kontrola zda je hráč nebo není na whitelistu")
                    .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče"))
                    .addOptions(new OptionData(OptionType.STRING, "ipaddress", "IP adresa hráče pro kontrolu"))));

        commands.addCommands(Commands.slash("notifications", "Vytváření notifikací pro hráče jednoduše z Discordu")
                .addSubcommands(new SubcommandData("create", "Vytvoření notifikace pro specifického hráče")
                        .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče").setRequired(true))
                        .addOptions(new OptionData(OptionType.STRING, "type", "Typ notifikace nebo-li kategorie").setRequired(true)
                                .addChoice("Info", "INFO")
                                .addChoice("Economy", "ECONOMY")
                                .addChoice("Admin Team", "ADMIN_TEAM")
                                .addChoice("Server", "SERVER")
                                //.addChoice("Error", "ERROR")
                                //.addChoice("Unknown", "UNKNOWN")
                        )
                        .addOptions(new OptionData(OptionType.STRING, "priority", "Priorita notifikace v 99% zvol vždy normalní").setRequired(true)
                                .addChoice("Normalní", "NORMAL")
                                .addChoice("Vyšší", "HIGHER")
                                .addChoice("Urgentní", "URGENT")
                        )
                        .addOptions(new OptionData(OptionType.STRING, "server", "Server na kterém platí notifikace").setRequired(true)
                                .addChoice("Všechny servery", "ALL")
                                .addChoice("Survival", "SURVIVAL")
                                .addChoice("Skyblock", "SKYBLOCK")
                                .addChoice("Creative", "CREATIVE")
                                .addChoice("Vanilla", "VANILLA")
                        )
                )
                .addSubcommands(new SubcommandData("tutorial", "Základní informace k vytvoření notifikací a jak je používat."))
                .addSubcommands(new SubcommandData("broadcast", "Odeslání notifikace hráčům, kteří byli na serveru za x dní"))
        );

        commands.addCommands(Commands.slash("at-reputation", "Hodnocení CraftMania Admin Teamu"));

        commands.addCommands(Commands.slash("rep", "Udělení reputace jinému hráči na serveru")
                .addOptions(new OptionData(OptionType.USER, "user", "Hráč jako Discord uživatel, kterému chceš dát reputaci").setRequired(true))
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
        registerSlashCommand(new LinkSlashCommand());
        registerSlashCommand(new UnlinkSlashCommand());
        registerSlashCommand(new RoomSlashCommand());
        registerSlashCommand(new WikiSlashCommand());
        registerSlashCommand(new UUIDSlashCommand());
        registerSlashCommand(new ProfileSlashCommand());
        registerSlashCommand(new RoleSlashCommand());
        registerSlashCommand(new ProfileSettingsSlashCommand());
        registerSlashCommand(new BoosterSlashCommand());
        registerSlashCommand(new AtReputationCommand());
        registerSlashCommand(new ReputationSlashCommand());

        // Fun příkazy
        registerSlashCommand(new ChooseSlashCommand());

        // Moderation
        registerSlashCommand(new CheckIpSlashCommand());
        registerSlashCommand(new StaffListSlashCommand());
        registerSlashCommand(new CheckBanSlashCommand());
        registerSlashCommand(new ATSSlashCommand());
        registerSlashCommand(new WhitelistSlashCommand());
        registerSlashCommand(new NotificationsSlashCommand());

        // Owner
        registerSlashCommand(new SetupCommand());
    }
}
