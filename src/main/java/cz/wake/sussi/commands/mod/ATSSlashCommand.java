package cz.wake.sussi.commands.mod;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.ats.ATS;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.SussiLogger;
import cz.wake.sussi.utils.TimeUtils;
import dev.mayuna.mayusjdautils.interactive.Interaction;
import dev.mayuna.mayusjdautils.interactive.components.InteractiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ATSSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {
        try {
            OptionMapping nameOption = event.getOption("name");
            OptionMapping userOption = event.getOption("user");

            if (nameOption == null && userOption == null) { // /ats
                if (Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
                    String name = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
                    if (!Sussi.getATSManager().isInATS(name)) {
                        MessageUtils.sendErrorMessage(MessageUtils.getEmbedError().setDescription("Nelze použít `/ats` pokud nejsi člen AT!"), hook);
                        return;
                    }
                    ATS ats = Sussi.getATSManager().getATS(name);
                    if (ats == null) {
                        MessageUtils.sendErrorMessage(MessageUtils.getEmbedError().setDescription("Nelze použít `/ats` pokud nejsi člen AT!"), hook);
                        return;
                    }
                    sendATS(ats, hook);
                    return;
                }
            }

            String nick = "";

            if (nameOption != null) { // /ats <name>
                nick = nameOption.getAsString();
            } else if (userOption != null) { // /ats @user
                if (Sussi.getInstance().getSql().isAlreadyLinkedByID(userOption.getAsUser().getId()))
                    nick = Sussi.getInstance().getSql().getLinkedNickname(userOption.getAsUser().getId());
                else {
                    MessageUtils.sendErrorMessage("Uživatel " + userOption.getAsUser().getAsMention() + " nemá propojený MC účet.", hook);
                    return;
                }
            }

            if (!Sussi.getATSManager().isInATS(nick)) {
                MessageUtils.sendErrorMessage(MessageUtils.getEmbedError().setDescription("Požadovaný člen není v AT nebo nebyl nalezen!"), hook);
                return;
            }

            ATS ats = Sussi.getATSManager().getATS(nick);

            if (ats == null) {
                MessageUtils.sendErrorMessage(MessageUtils.getEmbedError().setDescription("Požadovaný člen není v AT nebo nebyl nalezen!"), hook);
                return;
            }

            sendATS(ats, hook);
        } catch (Exception exception) {
            exception.printStackTrace();
            SussiLogger.errorMessage("Exception occurred while processing ATS Command!");

            MessageUtils.sendErrorMessage(MessageUtils.getEmbedError()
                    .setDescription("Nastala chyba při provádění ATS příkazu. Zkus to prosím později.")
                    .addField("Technické informace", "`" + exception + "`", false), hook);
        }
    }

    private void sendATS(ATS ats, InteractionHook hook) {
        //TODO: Vyřešit s mayu DiscordUtils.generateButton(ButtonStyle.SECONDARY, "Další strana")
        hook.sendMessageEmbeds(getFirstPageEmbed(ats).build());
        InteractiveMessage iMessage = InteractiveMessage.create(new MessageEditBuilder().setEmbeds(getFirstPageEmbed(ats).build()));
        iMessage.addInteraction(Interaction.asButton(ButtonStyle.SECONDARY, "Další strana"), (interactionEvent) -> {
            InteractiveMessage firstPageMessage = InteractiveMessage.create(new MessageEditBuilder().setEmbeds(getSecondPageEmbed(ats).build()));
            firstPageMessage.addInteraction(Interaction.asButton(ButtonStyle.SECONDARY, "Předchozí strana"), (interactionEvent2) -> {
                iMessage.editOriginal(hook, true);
            });
            firstPageMessage.editOriginal(hook, true);
        });
        iMessage.sendMessage(hook, true);
    }

    private EmbedBuilder getFirstPageEmbed(ATS ats) {
        return MessageUtils.getEmbed(ats.getColorByRank())
                .setTitle("Přehled ATS pro - " + ats.getName())
                .setThumbnail("https://mc-heads.net/head/" + ats.getName() + "/128.png")
                .addField("Rank", ats.getRankByID(), true)
                .addField("Přístup na Build", getResult(ats.getPristup_build()), true)
                .addField("Celkem hodin", TimeUtils.formatTime("%d dni, %hh %mm", ats.getTotalTime(), false), true)
                .addField("Celkem aktivita", ats.getTotalActivityFormatted(), true)
                .addField("Trestné body", String.valueOf(0), true)
                .addField("Min. počet hodin", ats.getMinHoursFormatted() + " (" + resolveTime(ats.getTotalTime() / 60, ats.getMin_hours()) + ")", true)
                .setFooter("Platné pro: " + getDate(System.currentTimeMillis()) + " (v3.1)", null);
    }

    private EmbedBuilder getSecondPageEmbed(ATS ats) {
        return MessageUtils.getEmbed(ats.getColorByRank())
                .setTitle("Přehled ATS pro - " + ats.getName())
                .addField("Survival",
                        "**Chat:** " + ats.getServerATS(ATS.Server.SURVIVAL).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm",
                                ats.getServerATS(ATS.Server.SURVIVAL).getPlayedTime(),
                                false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.SURVIVAL).getLastActivity()),
                        true)
                .addField("Skyblock",
                        "**Chat:** " + ats.getServerATS(ATS.Server.SKYBLOCK).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm",
                                ats.getServerATS(ATS.Server.SKYBLOCK).getPlayedTime(),
                                false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.SKYBLOCK).getLastActivity()),
                        true)
                .addField("Creative",
                        "**Chat:** " + ats.getServerATS(ATS.Server.CREATIVE).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm",
                                ats.getServerATS(ATS.Server.CREATIVE).getPlayedTime(),
                                false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.CREATIVE).getLastActivity()),
                        true)
                .addField("Prison",
                        "**Chat:** " + ats.getServerATS(ATS.Server.PRISON).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm",
                                ats.getServerATS(ATS.Server.PRISON).getPlayedTime(),
                                false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.PRISON).getLastActivity()),
                        true)
                .addField("Vanilla",
                        "**Chat:** " + ats.getServerATS(ATS.Server.VANILLA).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm",
                                ats.getServerATS(ATS.Server.VANILLA).getPlayedTime(),
                                false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.VANILLA).getLastActivity()),
                        true)
                .addField("Vanilla: Anarchy",
                        "**Chat:** " + ats.getServerATS(ATS.Server.VANILLA_ANARCHY).getChatBody() + "\n**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm",
                                ats.getServerATS(ATS.Server.VANILLA_ANARCHY).getPlayedTime(),
                                false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.VANILLA_ANARCHY).getLastActivity()),
                        true)
                .addField("Build servery",
                        "**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm",
                                ats.getServerATS(ATS.Server.BUILD).getPlayedTime(),
                                false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.BUILD).getLastActivity()),
                        true)
                .addField("Event server",
                        "**Odehráno:** " + TimeUtils.formatTime("%d dni, %hh %mm",
                                ats.getServerATS(ATS.Server.EVENTS).getPlayedTime(),
                                false) + "\n**Poslední aktivita:** " + getDate(ats.getServerATS(ATS.Server.EVENTS).getLastActivity()),
                        true)
                .setFooter("Platné pro: " + getDate(System.currentTimeMillis()) + " (v3)", null);
    }

    private String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
        return timeString;
    }

    private String getResult(int result) {
        if (result == 1) {
            return "Ano";
        }
        return "Ne";
    }

    private String resolveTime(int hours, int min) {
        if (hours >= min) {
            return "\u2705";
        }
        return "\u274C";
    }

    @Override
    public String getName() {
        return "ats";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getHelp() {
        return "ATS příkaz pro AT";
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
    public boolean isEphemeral() {
        return true;
    }
}
