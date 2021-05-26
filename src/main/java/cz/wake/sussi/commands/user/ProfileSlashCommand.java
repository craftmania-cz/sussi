package cz.wake.sussi.commands.user;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ISlashCommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.Profile;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.button.Button;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProfileSlashCommand implements ISlashCommand {

    @Override
    public void onSlashCommand(User sender, MessageChannel channel, Member member, InteractionHook hook, SlashCommandEvent event) {

        String nick = "";

        OptionMapping optionName = event.getOption("name");
        OptionMapping optionUser = event.getOption("user");

        if (optionName == null && optionUser == null) { // -> /profile
            if (!Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
                hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Špatně zadaný příkaz! Př. `/profile MrWakeCZ` nebo nemáš propojený profil.").build()).queue();
                return;
            } else nick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
        } else if (optionName != null) { // -> /profile [@nick]
            nick = optionName.getAsString();
        } else {
            User selectedUser = optionUser.getAsUser();
            if (Sussi.getInstance().getSql().isAlreadyLinkedByID(selectedUser.getId()))
                nick = Sussi.getInstance().getSql().getLinkedNickname(selectedUser.getId());
            else {
                hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Uživatel " + selectedUser.getAsMention() + " nemá propojený MC účet.").build()).queue();
                return;
            }
        }

        Profile profile = new Profile(nick);

        if (profile.getStatusId() == 404) {
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Hráč `" + nick + "` nebyl nalezen.").build()).queue();
            return;
        }

        if (profile.getStatusId() == 500) {
            hook.sendMessageEmbeds(MessageUtils.getEmbedError().setDescription("Nepodařilo se provést akci, zkus to zachvilku...").build()).queue();
            return;
        }

        firstPage(sender, hook, profile);
    }

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public String getDescription() {
        return "Zobrazení statistik z serveru";
    }

    @Override
    public String getHelp() {
        return "/profile [nick]";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    private void firstPage(User s, InteractionHook hook, Profile profile) {
        Color color = Color.WHITE;
        String role = "Hráč";

        if (Sussi.getInstance().getSql().isAT(profile.getName())) {
            int rank = Sussi.getInstance().getSql().getStalkerStats(profile.getName(), "rank");
            color = getColorByRank(rank);
            role = getRankByID(rank);
        } else if (profile.hasGlobalVIP()) {
            color = profile.getGlobalVIP().getVIPType().getColor();
        }

        EmbedBuilder embedBuilder = MessageUtils.getEmbed(color)
                .setTitle("Informace o hráči: " + profile.getName() + "#" + profile.getDiscriminator() + " (lvl: " + profile.getGlobal_level() + ")", "https://stats.craftmania.cz/player/" + profile.getName())
                .setThumbnail("https://mc-heads.net/head/" + profile.getName() + "/128.png")
                .addField("Globální statistiky",
                        "Registrován: " + getDate(profile.getRegistred()) + "\n" +
                                "Role: " + role + "\n" +
                                "Celkově odehraný čas: " + TimeUtils.formatTime("%d dni, %hh %mm", profile.getPlayedTime(), false) + "\n" +
                                getOnlineString(profile) +
                                (profile.isOnline() ? "" : "\nNaposledy viděn: " + getDate(profile.getLastOnline())) +
                                (profile.getDiscordID() == "" ? "" : "\nDiscord: <@" + profile.getDiscordID() + ">")
                        , false)
                .addField("Ekonomika",
                        "CraftCoins: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getCraftCoins()) + "\n" +
                                "CraftTokeny: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getCraftTokens()) + "\n" +
                                "VoteTokeny: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getVoteTokens()) + "\n" +
                                "Event pointy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getEventPoints()) + "\n" +
                                "Quest pointy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getAchievementPoints()) + "\n" +
                                "Bug pointy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getBugPoints())
                        , true)
                .addField("Hlasování",
                        "Celkem hlasů: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getTotalVotes()) + "\n" +
                                "Měsíční hlasy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getMonthVotes()) + "\n" +
                                "Týdenní hlasy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getWeekVotes()) + "\n" +
                                "Poslední hlas: " + getDate(profile.getLastVote()) + "\n" +
                                "VotePass: " + profile.getVotePass()
                        , true)
                .addField("Discord",
                        "Počet zpráv: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getDiscord_messages()) + "\n" + "Voice: " + TimeUtils.formatTime("%d dni, %hh %mm", profile.getDiscord_voice()/1000, false), true)
                .addField("Levely",
                        "Survival: " + profile.getSurvival_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getSurvival_experience()) + "XP)" + "\n" +
                                "SkyBlock: " + profile.getSkyblock_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getSkyblock_experience()) + "XP)" + "\n" +
                                "Creative: " + profile.getCreative_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getCreative_experience()) + "XP)" + "\n" +
                                "Prison: " + profile.getPrison_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getPrison_experience()) + "XP)" + "\n" +
                                "Vanilla [Lands]: " + profile.getVanilla_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getVanilla_experience()) + "XP)" + "\n" +
                                "Vanilla [Anarchy]: " + profile.getVanilla_anarchy_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getVanilla_anarchy_experience()) + "XP)" + "\n" +
                                "Staré servery: " + profile.getStare_servery_level() + "\n"
                        , true)
                .setFooter("CraftMania.cz Stats")
                .setTimestamp(Instant.from(ZonedDateTime.now()));

        if (profile.hasAnyVIP()) {
            embedBuilder.addField("VIP",
                    (profile.hasGlobalVIP() ? "Global: " + StringUtils.capitalize(profile.getGlobalVIP().getGroup()) + " VIP " + (profile.getGlobalVIP().isPermanent() ? "\n" : "(expirace: " + profile.getGlobalVIP().getFormattedDate() + ")\n") : "") +
                            (profile.getHighestVIPs().stream().map(vip -> vip.getServerName() + ": " + StringUtils.capitalize(vip.getGroup()) + " VIP " + (vip.isPermanent() ? "\n" : "(expirace: " + vip.getFormattedDate() + ")\n")).collect(Collectors.joining())),
                    true);
        }

        hook.sendMessageEmbeds(embedBuilder.build()).addActionRow(Button.link("https://stats.craftmania.cz/player/" + profile.getName(), "Profil na Stats")).queue();
    }

    private Color getColorByRank(int rank){
        if(rank == 12){
            return Constants.MAJITEL;
        } else if (rank == 11){
            return Constants.MANAGER;
        } else if (rank == 10){
            return Constants.HL_ADMIN;
        } else if (rank == 9){
            return Constants.DEV;
        } else if (rank == 2 || rank == 3){
            return Constants.HELPER;
        } else if (rank == 4 || rank == 5){
            return Constants.ADMIN;
        } else if (rank == 7){
            return Constants.EVENTER;
        } else if (rank == 8){
            return Constants.MOD;
        } else if (rank == 6){
            return Constants.BUILDER;
        } else {
            return Constants.GRAY;
        }
    }

    private String getRankByID(int rank){
        if(rank == 12){
            return "Majitel";
        } else if (rank == 11){
            return "Manager";
        } else if (rank == 10){
            return "Hl.Admin";
        } else if (rank == 9){
            return "Developer";
        } else if (rank == 2){
            return "Helper";
        } else if (rank == 3){
            return "Helperka";
        } else if (rank == 4){
            return "Admin";
        } else if (rank == 5){
            return "Adminka";
        } else if (rank == 7){
            return "Eventer";
        } else if (rank == 8){
            return "Moderátor";
        } else if (rank == 6){
            return "Builder";
        } else {
            return "Hajzlík s chybným ID!";
        }
    }

    private String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy").format(cal.getTime());
        return timeString;
    }

    private String getDateWithTime(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
        return timeString;
    }

    private String getOnlineString(Profile profile) {
        if (profile.isOnline()) {
            return "Online: Ano (server: " + profile.getLastServer() + ")";
        }
        return "Poslední server: " + profile.getLastServer() + "";
    }
}
