package cz.wake.sussi.commands;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.mod.*;
import cz.wake.sussi.commands.owner.SetupCommand;
import cz.wake.sussi.commands.user.*;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
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

        commands.addCommands(new CommandData("help", "Nápověda k Sussi"));
        commands.addCommands(new CommandData("unlink", "Odpojení svého CraftMania profilu od tvého Discord účtu."));

        commands.addCommands(new CommandData("link", "Propojení CraftMania účtu s tvým Discord účtem.")
                .addOptions(new OptionData(OptionType.STRING, "key", "Klíč, který se ti zobrazit skrz /link příkaz na serveru").setRequired(true)));

        commands.addCommands(new CommandData("room", "Spravování vlastní hlasové místnosti")
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

        commands.addCommands(new CommandData("wiki", "Rychlé odkazy na témata a návody na naší Wiki")
                .addOptions(new OptionData(OptionType.STRING, "id", "Výběr tématu nebo návodu z Wiki").setRequired(true)
                    .addChoice("Pravidla serveru", "pravidla")
                    .addChoice("Povolené a zakázané módy", "povolene-mody")
                    .addChoice("Jak se připojit na server", "jak-se-pripojit")
                    .addChoice("Problémy s resource packem", "problemy-s-resource-packem")
                    .addChoice("Návod: Jak na Craftmania Discord", "navod-discord")
                    .addChoice("Návod: Jak najít screeny, logy", "navod-screeny-a-logy")));

        commands.addCommands(new CommandData("choose", "Vyber nějaké možnosti a nech na Sussi ať vybere tu správnou.")
                .addOptions(new OptionData(OptionType.STRING, "opt1", "První hodnota na zvolení").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "opt2", "Druhá hodnota na zvolení").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "opt3", "Třetí hodnota na zvolení"))
                .addOptions(new OptionData(OptionType.STRING, "opt4", "Čtvrtá hodnota na zvolení"))
                .addOptions(new OptionData(OptionType.STRING, "opt5", "Pátá hodnota na zvolení"))
                .addOptions(new OptionData(OptionType.STRING, "opt6", "Šestí hodnota na zvolení")));

        commands.addCommands(new CommandData("checkip", "Kontrola IP adresy, zda je VPN, z jakého je kraje a jiné informace.")
                .addOptions(new OptionData(OptionType.STRING, "ip", "IP adresa ve formát IPv4 nebo IPv6"))
                .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče, který bude zkontrolován")));

        commands.addCommands(new CommandData("uuid", "Získání online a offline UUID podle nicku hráče.")
                .addOptions(new OptionData(OptionType.STRING, "name", "Minecraft nick hráče").setRequired(true)));

        commands.addCommands(new CommandData("profile", "Zobrazení CraftMania statistik a hráčského profilu.")
                .addOptions(new OptionData(OptionType.STRING, "name", "Minecraft nick hráče z serveru"))
                .addOptions(new OptionData(OptionType.USER, "user", "Discord uživatel, který má propojený účet.")));

        commands.addCommands(new CommandData("profile-settings", "Nastavení svého profilu pro statistiky na serveru.")
                .addSubcommands(new SubcommandData("status", "Nastavení statusu pro svůj profil na serveru")
                    .addOptions(new OptionData(OptionType.STRING, "text", "Status, který se nastaví na tvůj profil.").setRequired(true)))
                .addSubcommands(new SubcommandData("gender", "Nastavení pohlaví pro svůj profil")
                    .addOptions(new OptionData(OptionType.STRING, "type", "Zvolení pohlaví, co se nastaví na profil").setRequired(true)
                        .addChoice("Muž", "1")
                        .addChoice("Žena", "2")
                        .addChoice("Nezvoleno", "0"))));

        commands.addCommands(new CommandData("checkban", "Zkontrolování zda má hráč aktivní ban a odkoho.")
                //.addOptions(new OptionData(OptionType.STRING, "ip", "IP adresa ve formát IPv4 nebo IPv6"))
                .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče, který bude zkontrolován").setRequired(true)));

        commands.addCommands(new CommandData("stafflist", "Seznam členů AT a jejich ID pro přidání do ticketů."));

        commands.addCommands(new CommandData("ats", "Zobrazení statistik o tom, jak který člen AT je aktivní na serveru.")
                        .addOptions(new OptionData(OptionType.STRING, "name", "Jméno člena AT k zobrazení.", false))
                        .addOptions(new OptionData(OptionType.USER, "user", "Uživatel z AT k zobrazení", false)));
                        
        commands.addCommands(new CommandData("booster", "Nastavění vlastní booster barvy.")
                .addOptions(new OptionData(OptionType.STRING, "hex_color", "Barva ve formátu hex.").setRequired(true)));

        commands.addCommands(new CommandData("setup", "Nastavení specifických nastavení na Discord serveru")
                .addOptions(new OptionData(OptionType.STRING, "type", "Hodnota k nastavení").setRequired(true)
                        .addChoice("Výběr rolí", "role-selector")));

        commands.addCommands(new CommandData("whitelist", "Správa whitelistu serveru, pokud se připojuje na server mimo CZ/SK.")
                .addSubcommands(new SubcommandData("add", "Přidání hráče na whitelist")
                    .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče").setRequired(true))
                    .addOptions(new OptionData(OptionType.STRING, "ipaddress", "IP adresa hráče, lze použít IPv4 i IPv6").setRequired(true)))
                .addSubcommands(new SubcommandData("remove", "Odebrání hráče z whitelistu")
                    .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče"))
                    .addOptions(new OptionData(OptionType.STRING, "ipaddress", "IP adresa hráče, lze použít IPv4 i IPv6")))
                .addSubcommands(new SubcommandData("check", "Kontrola zda je hráč nebo není na whitelistu")
                    .addOptions(new OptionData(OptionType.STRING, "name", "Jméno hráče"))
                    .addOptions(new OptionData(OptionType.STRING, "ipaddress", "IP adresa hráče pro kontrolu")))
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
        registerSlashCommand(new UUIDSlashCommand());
        registerSlashCommand(new ProfileSlashCommand());
        registerSlashCommand(new RoleSlashCommand());
        registerSlashCommand(new ProfileSettingsSlashCommand());
        registerSlashCommand(new BoosterSlashCommand());

        // Fun příkazy
        registerSlashCommand(new ChooseSlashCommand());

        // Moderation
        registerSlashCommand(new CheckIpSlashCommand());
        registerSlashCommand(new StaffListSlashCommand());
        registerSlashCommand(new CheckBanSlashCommand());
        registerSlashCommand(new ATSSlashCommand());
        registerSlashCommand(new WhitelistSlashCommand());

        // Owner
        registerSlashCommand(new SetupCommand());
    }
}
