package cz.wake.sussi.commands.owner;

import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

public class SetupCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {
        String type = event.getOption("type").getAsString();

        switch (type) {
            case "role-selector" -> {
                channel.sendMessage("https://cdn.discordapp.com/attachments/794379366280724570/995482893721796709/discord_role.png").queue(); // Obrázek
                channel.sendMessage("Zde si můžeš nastavit vlastní role, které chceš. Některé ti zpřístupní speciální kanály, některé jsou pouze kosmetické. Všechny si můžeš kdykoliv odebrat tím že roli vybereš znova.").queue();
                channel.sendMessage(".").queue();
                this.generateThemeRoleSelector(channel);
                channel.sendMessage(".").queue();
                this.generateAnnounceRoleSelector(channel);
                channel.sendMessage(".").queue();
                this.generateServerRoleSelector(channel);
                hook.deleteOriginal().queue();
            }
        }
    }

    private void generateThemeRoleSelector(MessageChannel channel) {
        SelectMenu menu = SelectMenu.create("menu:role_theme")
                .setPlaceholder("Zvol si roli pro kanál s specifickým tématem")
                .setMaxValues(1)
                .setMinValues(1)
                .addOption("Apple", "apple", "Pokec o Applu a o tom kdo ztratil airpody", Emoji.fromFormatted("<:apple:995454047375065198>"))
                .addOption("Fortnite", "fortnite", "Novinky z Fortnitu a leaky", Emoji.fromFormatted("<:fortnite:995454225561698334>"))
                .addOption("Hytale", "hytale", "Čekání na novou hru Hytale (snad někdy)", Emoji.fromFormatted("<:hytale:995454406793375795>"))
                .build();
        StringBuilder description = new StringBuilder();
        description.append("\n\n**Tématické role**").append("\n");
        description.append("Tématické role slouží k zpřístupnění kanálů s určeným tématem nebo kanály s novinkami z her a jiných.");
        channel.sendMessage(description.toString()).setActionRow(menu).queue();
    }

    private void generateAnnounceRoleSelector(MessageChannel channel) {
        SelectMenu menu = SelectMenu.create("menu:role_announce")
                .setPlaceholder("Zvol si roli od které budeš dosávat upozornění")
                .setMaxValues(1)
                .setMinValues(1)
                .addOption("News", "news", "Novinky z CraftManie, malé změny a jiné informace", Emoji.fromFormatted("<a:peepoOH:638776117590753290>"))
                .addOption("Events", "events", "Oznámení o startu eventů na Event serveru", Emoji.fromFormatted("<a:peepoTub:945650150649507870>"))
                .addOption("Údržba", "udrzba", "Oznámení o plánovaných údržbách na serveru", Emoji.fromFormatted("<a:zabickaAlarm:954242184511619113>"))
                .build();
        StringBuilder description = new StringBuilder();
        description.append("\n\n**Oznamovací role**").append("\n");
        description.append("Každá z těchto rolí tě bude upozorňovat na něco jiného z našeho Minecraft serveru. Zvol si jednu nebo všechny a červenýho upozornění u ikony se už nezbavíš!");
        channel.sendMessage(description.toString()).setActionRow(menu).queue();
    }

    private void generateServerRoleSelector(MessageChannel channel) {
        SelectMenu menu = SelectMenu.create("menu:role_server")
                .setPlaceholder("Specifické kanály pro naše Minecraft servery")
                .setMaxValues(1)
                .setMinValues(1)
                .addOption("Survival [1.18]", "survival-118", "Kanál k diskuzi s hráči hrající na našem Survivalu")
                .build();
        StringBuilder description = new StringBuilder();
        description.append("\n\n**Server role**").append("\n");
        description.append("Role, které ti zpřístupní kanály k našim Minecraft serverům. K zpřístupnění těchto kanálů musíš mít na zvoleném serveru min. server level 5. Svůj level zjistíš v `/profile` nebo na serveru v `/level`.");
        channel.sendMessage(description.toString()).setActionRow(menu).queue();
    }

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTARTOR;
    }

    @Override
    public Rank getRank() {
        return Rank.ADMINISTRATOR;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
