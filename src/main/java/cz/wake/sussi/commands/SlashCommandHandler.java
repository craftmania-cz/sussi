package cz.wake.sussi.commands;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.mod.CheckIpSlashCommand;
import cz.wake.sussi.commands.user.*;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SlashCommandHandler {

    private CommandUpdateAction commands;
    public static List<ISlashCommand> list = new ArrayList<>();

    public SlashCommandHandler() {
        this.commands = Sussi.getJda().updateCommands();
    }

    public void createAndUpdateCommands() {

        commands.addCommands(new CommandData("help", "Nápověda k Sussi"));
        commands.addCommands(new CommandData("unlink", "Odpojení svého CraftMania profilu od tvého Discord účtu."));

        commands.addCommands(new CommandData("link", "Propojení CraftMania účtu s tvým Discord účtem.")
                .addOption(new OptionData(OptionType.STRING, "key", "Klíč, který se ti zobrazit skrz /link příkaz na serveru").setRequired(true)));

        commands.addCommands(new CommandData("standa", "Standův tajný příkaz, chceš čokošku?"));

        commands.addCommands(new CommandData("room", "Spravování vlastní hlasové místnosti")
                .addSubcommand(new SubcommandData("help", "Zobrazení nápovědy pro spravování voice kanálu"))
                .addSubcommand(new SubcommandData("lock", "Uzamknutí voice místnosti"))
                .addSubcommand(new SubcommandData("unlock", "Odemknutí voice místnosti"))
                .addSubcommand(new SubcommandData("add", "Přidání práv pro uživatele do uzamknuté místnosti")
                    .addOption(new OptionData(OptionType.USER, "user", "Uživatel, kterému budou dány práva do voice").setRequired(true)))
                .addSubcommand(new SubcommandData("remove", "Odebrání práv pro uživatele z místnosti")
                    .addOption(new OptionData(OptionType.USER, "user", "Uživatel, kterému budou odebrány práva do voice").setRequired(true)))
                .addSubcommand(new SubcommandData("name", "Změnit název voice místnosti")
                    .addOption(new OptionData(OptionType.STRING, "text", "Nový název místnosti").setRequired(true)))
                .addSubcommand(new SubcommandData("bitrate", "Nastavení kvality hovoru v místnosti.")
                    .addOption(new OptionData(OptionType.INTEGER, "value", "Hodnota kbps, která se nastaví pro místnost.").setRequired(true)))
                .addSubcommand(new SubcommandData("unlimited", "Nastaví neomezený počet připojení do místnosti (limit 99)."))
                .addSubcommand(new SubcommandData("limit", "Nastaví limit uživatelů v místnosti.")
                    .addOption(new OptionData(OptionType.INTEGER, "value", "Limit uživatelů, který se nastaví pro místnost.").setRequired(true)))
                .addSubcommand(new SubcommandData("ban", "Zablokování a skryté voice místnosti pro uživatele")
                    .addOption(new OptionData(OptionType.USER, "user", "Uživatel, který bude zabanován z místnosti.").setRequired(true)))
                .addSubcommand(new SubcommandData("unban", "Zrušení blokace přístupu uživateli do voice místnosti.")
                    .addOption(new OptionData(OptionType.USER, "user", "Uživatel, který bude mít opět přístup do místnosti.").setRequired(true)))
                .addSubcommand(new SubcommandData("kick", "Vyhození uživatele z místnosti")
                    .addOption(new OptionData(OptionType.USER, "user","Uživatel, který bude vyhozen z místnosti").setRequired(true))));

        commands.addCommands(new CommandData("wiki", "Rychlé odkazy na témata a návody na naší Wiki")
                .addOption(new OptionData(OptionType.STRING, "id", "Výběr tématu nebo návodu z Wiki").setRequired(true)
                    .addChoice("Pravidla serveru", "pravidla")
                    .addChoice("Povolené a zakázané módy", "povolene-mody")
                    .addChoice("Jak se připojit na server", "jak-se-pripojit")
                    .addChoice("Problémy s resource packem", "problemy-s-resource-packem")
                    .addChoice("Návod: Jak na Craftmania Discord", "navod-discord")
                    .addChoice("Návod: Jak najít screeny, logy atd.", "navod-screeny-a-logy")));

        commands.addCommands(new CommandData("choose", "Vyber nějaké možnosti a nech na Sussi ať vybere tu správnou.")
                .addOption(new OptionData(OptionType.STRING, "opt1", "První hodnota na zvolení").setRequired(true))
                .addOption(new OptionData(OptionType.STRING, "opt2", "Druhá hodnota na zvolení").setRequired(true))
                .addOption(new OptionData(OptionType.STRING, "opt3", "Třetí hodnota na zvolení"))
                .addOption(new OptionData(OptionType.STRING, "opt4", "Čtvrtá hodnota na zvolení"))
                .addOption(new OptionData(OptionType.STRING, "opt5", "Pátá hodnota na zvolení"))
                .addOption(new OptionData(OptionType.STRING, "opt6", "Šestí hodnota na zvolení")));

        commands.addCommands(new CommandData("checkip", "Kontrola IP adresy, zda je VPN, z jakého je kraje a jiné informace.")
                .addOption(new OptionData(OptionType.STRING, "ip", "IP adresa ve formát IPv4 nebo IPv6"))
                .addOption(new OptionData(OptionType.STRING, "name", "Jméno hráče, který bude zkontrolován")));

        commands.addCommands(new CommandData("uuid", "Získání online a offline UUID podle nicku hráče.")
                .addOption(new OptionData(OptionType.STRING, "name", "Minecraft nick hráče").setRequired(true)));

        commands.addCommands(new CommandData("profile", "Zobrazení CraftMania statistik a hráčského profilu.")
                .addOption(new OptionData(OptionType.STRING, "name", "Minecraft nick hráče z serveru"))
                .addOption(new OptionData(OptionType.USER, "user", "Discord uživatel, který má propojený účet.")));

        commands.addCommands(new CommandData("profile-settings", "Nastavení svého profilu pro statistiky na serveru.")
                .addSubcommand(new SubcommandData("status", "Nastavení statusu pro svůj profil na serveru")
                    .addOption(new OptionData(OptionType.STRING, "text", "Status, který se nastaví na tvůj profil.").setRequired(true)))
                .addSubcommand(new SubcommandData("gender", "Nastavení pohlaví pro svůj profil")
                    .addOption(new OptionData(OptionType.STRING, "type", "Zvolení pohlaví, co se nastaví na profil").setRequired(true)
                        .addChoice("Muž", "man")
                        .addChoice("Žena", "woman")
                        .addChoice("Nezvoleno", "no-gender"))));

        commands.addCommands(new CommandData("role", "Nastavení role pro přístup do kanálů nebo dostávání upozornění")
                .addOption(new OptionData(OptionType.STRING, "name", "Zvol si roli, kterou si chceš přidat nebo odebrat.").setRequired(true)
                    .addChoice("News (oznámení o novinkách)", "news")
                    .addChoice("Events (oznámení o eventech)", "events")
                    .addChoice("Apple", "apple")
                    .addChoice("Android", "android")
                    .addChoice("Crypto", "crypto")
                    .addChoice("Fortnite", "fortnite")
                    .addChoice("Hytale", "hytale")
                    .addChoice("Genshin Impact", "genshin")
                    .addChoice("Osu", "osu")
                    .addChoice("GTA", "gta")
                    .addChoice("Korean (Kanál v jiném jazyce)", "korean")));

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

        // Fun příkazy
        registerSlashCommand(new StandaSlashCommand());
        registerSlashCommand(new ChooseSlashCommand());

        // Moderation
        registerSlashCommand(new CheckIpSlashCommand());
    }






}
