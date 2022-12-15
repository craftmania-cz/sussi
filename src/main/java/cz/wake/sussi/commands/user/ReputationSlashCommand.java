package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.Profile;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class ReputationSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandInteractionEvent event) {

        if (!Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Nemáš propojený účet, nemůžeš tedy hodnotit ostatní hráče. Propoj sis jej v kanálu #propojeni_mc_profilu").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("User does not have connected profile to Craftmania Profile, reputation blocked.");
            return;
        }

        String minecraftNick = Sussi.getInstance().getSql().getMinecraftNick(sender.getId());
        Profile profile = new Profile(minecraftNick);
        if (profile.getGlobal_level() < 2) {
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Nemáš dostatečný Globalní Level. Možnost hodnotit členy AT je zpřístupněno od levelu 2, ty máš: **" + profile.getGlobal_level() + "**").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("User does not have global level higher then 2, reputation blocked.");
            return;
        }

        long lastReputationTime = Sussi.getInstance().getSql().getLastKarmaGivenTime(sender.getIdLong());
        long nextReputationTime = lastReputationTime + TimeUnit.HOURS.toMillis(24);
        if (nextReputationTime >= System.currentTimeMillis()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(nextReputationTime), ZoneOffset.ofHours(+1)).format(formatter);
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Reputaci lze dávat pouze 1x za 24 hodin. Nejbližší možný čas **" + dateTime + "**.").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("User is in cooldown for player reputation. Time: " + dateTime + ", reputation blocked.");
            return;
        }

        User selectedUser = event.getOption("user").getAsUser();
        String selectedUserMinecraftNick = Sussi.getInstance().getSql().getMinecraftNick(selectedUser.getId());

        if (selectedUserMinecraftNick == null) {
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Zadaný uživatel nemá propojený účet, nelze mu dát reputaci.").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("Required user does not have linked profile, reputation blocked.");
            return;
        }

        if (selectedUser == sender) {
            hook.sendMessageEmbeds(new EmbedBuilder().setTitle("Chyba").setDescription("Sám sobě dát reputaci nemůžeš!").setColor(Constants.ADMIN).build()).setEphemeral(true).queue();
            SussiLogger.infoMessage("User gave reputation yourself, reputation blocked.");
            return;
        }

        SussiLogger.infoMessage("[REPUTATION] User " + sender.getAsTag() + "(" + sender.getId() + ") gave reputation to user " + selectedUser.getAsTag() + "(" + selectedUser.getId() + ")");
        Sussi.getInstance().getSql().updateKarmaStatistics(sender.getIdLong(), selectedUser.getIdLong(), 1);
        Sussi.getInstance().getSql().createReputationLog(selectedUserMinecraftNick, selectedUser.getIdLong(), minecraftNick, sender.getIdLong(), 1, null);

        hook.sendMessage("Dal jsi hráči **" + selectedUserMinecraftNick + "** (" + selectedUser.getAsMention() + ") reputaci.").setEphemeral(false).queue();
    }

    @Override
    public String getName() {
        return "reputation";
    }

    @Override
    public String getDescription() {
        return "Player reputation";
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
}
