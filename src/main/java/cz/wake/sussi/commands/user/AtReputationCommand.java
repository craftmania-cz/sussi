package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.Profile;
import cz.wake.sussi.objects.ats.AtsRatingObject;
import cz.wake.sussi.objects.ats.AtsUtils;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.SussiLogger;
import dev.mayuna.mayusjdautils.interactive.Interaction;
import dev.mayuna.mayusjdautils.interactive.components.InteractiveMessage;
import dev.mayuna.mayusjdautils.interactive.components.InteractiveModal;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AtReputationCommand implements ISlashCommand {

    private final AtsUtils atsUtils = new AtsUtils();

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {

        if (channel.getIdLong() != 1051917159674155038L) { // at-reputation CM channel
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("V tomto kanálu nelze používat tento příkaz.").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("User wrote command in wrong channel, blocked. This is mistake in server settings.");
            return;
        }

        if (!Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Nemáš propojený účet, nemůžeš tedy hodnotit členy AT. Propoj sis jej v kanálu #propojeni_mc_profilu").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("User does not have connected profile to Craftmania Profile, review blocked.");
            return;
        }

        String minecraftNick = Sussi.getInstance().getSql().getMinecraftNick(sender.getId());
        Profile profile = new Profile(minecraftNick);
        if (profile.getGlobal_level() < 3) {
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Nemáš dostatečný Globalní Level. Možnost hodnotit členy AT je zpřístupněno od levelu 3, ty máš: **" + profile.getGlobal_level() + "**").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("User does not have global level higher then 3, review blocked.");
            return;
        }

        if (Sussi.getInstance().getSql().isAT(minecraftNick)) {
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Člen AT nemůže hodnotit jiné členy AT.").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("AT member can't review other members, review blocked.");
            return;
        }

        long lastReputationTime = Sussi.getInstance().getSql().getLastKarmaGivenTime(sender.getIdLong());
        long nextReputationTime = lastReputationTime + TimeUnit.HOURS.toMillis(48);
        if (nextReputationTime >= System.currentTimeMillis()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(nextReputationTime), ZoneOffset.ofHours(+1)).format(formatter);
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Členy AT lze hodnotit pouze 1x za 48 hodin. Nejbližší možný čas **" + dateTime + "**.").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("User is in cooldown for AT review. Time: " + dateTime + ", review blocked.");
            return;
        }

        MessageEditBuilder messageBuilder = new MessageEditBuilder();
        messageBuilder.setEmbeds(
                new EmbedBuilder().setTitle("Hodnocení Admin Teamu")
                        .setDescription("V výběru níže, vyber člena AT, kterého chceš ohodnotit. Poté se ti otevře menu, ve kterém můžeš udělat počet hvězdiček a svůj komentář k tomu.")
                        .addField("Platné hodnocení", "⭐⭐⭐⭐⭐ (5) - Nejlepší\n⭐⭐⭐⭐ (4) -> Velmi dobrý\n⭐⭐⭐ (3) -> Dobrý\n⭐⭐ (2) -> Špatný\n⭐ (1) -> Velmi špatný", true)
                        .addField("Text", "Maximální počet znaků - 2000", true)
                        .setFooter("Pokud menu zavřeš, ztratíš i text!")
                        .setColor(Constants.LIGHT_BLUE)
                        .build());

        InteractiveMessage message = InteractiveMessage.createStringSelectMenu(messageBuilder, "Zvol člena AT, kterého chceš hodnotit");

        List<AtsRatingObject> atsRatingObjectList = Sussi.getInstance().getSql().getAtsRatingList();
        atsRatingObjectList.forEach(atsMember -> {
            message.addInteraction(Interaction.asSelectOption(atsMember.getName(), atsUtils.getRankByID(atsMember.getRank()), Emoji.fromFormatted(atsMember.getHeadEmoji())), (onInteracted) -> {
                Modal.Builder modalBuilder = Modal.create(UUID.randomUUID().toString(), "Hodnocení člena AT: " + atsMember.getName());

                TextInput starsAmount = TextInput.create("starsAmount", "Hodnocení v rozmezí 1-5 (5 nejlepší)", TextInputStyle.SHORT)
                        .setPlaceholder("Počet hvězdiček od 1 do 5")
                        .setRequiredRange(1, 1)
                        .setRequired(true)
                        .build();

                TextInput atReview = TextInput.create("atReview", "Komentář k hodnocení", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Zde napiš delší text k hodnocení člena. Pozor ale, jak menu zavřeš, ztratíš text.")
                        .setMaxLength(2000)
                        .setRequired(true)
                        .build();

                modalBuilder.addActionRow(starsAmount, atReview);
                InteractiveModal interactiveModal = InteractiveModal.create(modalBuilder, (onModalClosed) -> {
                    String starsSelected = onModalClosed.getValue("starsAmount").getAsString();
                    String reviewSelected = onModalClosed.getValue("atReview").getAsString();
                    if (!isNumber(starsSelected)) {
                        MessageEmbed errorEmbed = new EmbedBuilder().setTitle("Chyba").setDescription("Zadané hodnocení není číslo nebo má neplatnou hodnotu v rozmezí 1 až 5. Zkus to znovu, níže si můžeš okopírovat napsané hodnocení.\n```" + reviewSelected + "```").setColor(Constants.ADMIN).build();
                        onModalClosed.getHook().sendMessageEmbeds(errorEmbed).setEphemeral(true).queue();
                        return;
                    }
                    int starsAsInteger = Integer.parseInt(starsSelected);
                    SussiLogger.infoMessage("[AT RATING]: " + atsMember.getName() + " - hodnocení: " + starsAsInteger + " review: " + reviewSelected);
                    Sussi.getInstance().getSql().updateKarmaStatistics(sender.getIdLong(), atsMember.getDiscordId(), getKarmaPointsBasedStars(starsAsInteger));
                    Sussi.getInstance().getSql().createReputationLog(atsMember.getName(), atsMember.getDiscordId(), minecraftNick, sender.getIdLong(), starsAsInteger, reviewSelected);
                    onModalClosed.getChannel().sendMessage(Sussi.getJda().getUserById(atsMember.getDiscordId()).getAsMention()).queue();
                    onModalClosed.getChannel().sendMessageEmbeds(
                            new EmbedBuilder().setAuthor("Hodnocení Admin Teamu").setTitle(atsMember.getName() + " | " + resolveStars(starsAsInteger) + " (" + starsAsInteger + ")")
                                    .setColor(resolveColorBasedStars(starsAsInteger)).setDescription(reviewSelected).setFooter("Ohodnotil: " + member.getUser().getAsTag() + "(" + minecraftNick + ")")
                                    .build())
                            .queue();
                });

                interactiveModal.replyModal(onInteracted.getStringSelectInteractionEvent()).queue();
            });
        });

        message.sendMessage(hook, true);
    }

    @Override
    public String getName() {
        return "at-reputation";
    }

    @Override
    public String getDescription() {
        return "Ohodnocení AT";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    private boolean isNumber(String stringNumber) {
        if (stringNumber == null) {
            return false;
        }
        try {
            Integer.parseInt(stringNumber);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

    private String resolveStars(int stars) {
        switch (stars) {
            case 1 -> {
                return "⭐";
            }
            case 2 -> {
                return "⭐⭐";
            }
            case 3 -> {
                return "⭐⭐⭐";
            }
            case 4 -> {
                return "⭐⭐⭐⭐";
            }
            case 5 -> {
                return "⭐⭐⭐⭐⭐";
            }
            default -> {
                return "ERROR";
            }
        }
    }

    private Color resolveColorBasedStars(int stars) {
        switch (stars) {
            case 1, 2 -> {
                return Constants.RED;
            }
            case 3 -> {
                return Constants.DEV;
            }
            case 4, 5 -> {
                return Constants.GREEN;
            }
            default -> {
                return Constants.GRAY;
            }
        }
    }

    private int getKarmaPointsBasedStars(int stars) {
        switch (stars) {
            case 2 -> {
                return 1;
            }
            case 3,4 -> {
                return 2;
            }
            case 5 -> {
                return 3;
            }
            default -> {
                return 0;
            }
        }
    }
}
