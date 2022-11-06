package cz.wake.sussi.commands.mod;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.NotificationCacheObject;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.UUID;

public class NotificationsSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {

        String subcommandName = event.getSubcommandName();

        if (subcommandName == null) {
            return;
        }

        switch (subcommandName) {
            case "create" -> {
                String playerName = event.getOption("name").getAsString();
                String notificationType = event.getOption("type").getAsString();
                String notificationPriority = event.getOption("priority").getAsString();
                String notificationServer = event.getOption("server").getAsString();

                String playerUUID;

                if (Sussi.getInstance().getSql().existsPlayer(playerName)) {
                    playerUUID = Sussi.getInstance().getSql().getPlayerUUID(playerName);
                } else {
                    event.replyEmbeds(new EmbedBuilder().setColor(Constants.RED).setDescription("Hráč " + playerName + " se nenachází v databázi").build()).queue();
                    return;
                }

                TextInput title = TextInput.create("title", "Nadpis", TextInputStyle.SHORT)
                        .setPlaceholder("Zde napiš krátký název notifikace")
                        .setMinLength(3)
                        .setMaxLength(25)
                        .build();

                TextInput description = TextInput.create("description", "Text", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Zde napiš text notifikace bez použití barev")
                        .setMinLength(10)
                        .setMaxLength(1000)
                        .build();

                String modalUUID = UUID.randomUUID().toString();
                String modalId = "notification_create:" + modalUUID;
                Modal.Builder modal = Modal.create(modalId, "Vytvoření notifikace")
                        .addActionRows(ActionRow.of(title), ActionRow.of(description));

                String finalPlayerUUID = playerUUID;
                event.replyModal(modal.build()).queue((success) -> {
                    Sussi.getInstance().getNotificationCache().put(modalUUID, new NotificationCacheObject(modalId, playerName, finalPlayerUUID, notificationType, notificationPriority, notificationServer));
                    SussiLogger.infoMessage("Uložena notifikace do cache s ID: " + modalId);
                });
            }
            case "tutorial" -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Notifikace a jak je používat");
                embed.setColor(Constants.DEV);
                embed.addField("Základní informace", "Notifikace slouží k zasílání upozornění nebo oznámení hráčům, když jsou online nebo offline (nezáleží na tom).\nServer zasílá dle některých akcí notifikace hráčům sám, avšak pokud chceš vytvořit specificky pro někoho notifikace tak k tomu slouží tento příkaz.", false);
                embed.addField("Použití", "Jednoduše napiš příkaz `/notifications create` a vyplň nick, prioritu a platný server. Poté se ti otevře menu kde můžeš vyplnit nadpis a text.", false);
                embed.addField("Priority notifikací", "・**Normalní** - Slouží k normální informování hráče\n・**Vyšší** - Slouží k zdůraznění vážnosti avšak vypadá podobně jako normalní.\n・**Urgentní** - Speciální typ notifikace, který hráči nad inventářem ukáže text, že si musí notifikaci urgentně přečíst.", false);
                embed.addField("Výběr serveru a další akce", "U každé notifikace můžeš zvolit platnost notifikace pro daný server. Aktuálně to nemá žádný vliv, později ale může.", false);
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
            case "broadcast" -> {
                event.reply("Tato funkce není stále připravena k použití. Kdo počká, ten se dočká...").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public String getName() {
        return "notifications";
    }

    @Override
    public String getDescription() {
        return "Vytváření notifikací";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    @Override
    public boolean defferReply() {
        return false;
    }
}
