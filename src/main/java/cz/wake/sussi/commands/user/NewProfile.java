package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.Profile;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NewProfile implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        String nick = "";
        if(args.length < 1) {
            if (!Sussi.getInstance().getSql().isAlreadyLinkedByID(sender.getId())) {
                MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Př. `,profile MrWakeCZ`", channel);
                return;
            } else nick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
        }

        if (args.length > 0 || message.getMentions(Message.MentionType.USER).size() > 0) {
            if (args[0].startsWith("<@") && args[0].endsWith(">")) {
                List<IMentionable> mentions = message.getMentions(Message.MentionType.USER);
                if (mentions.size() > 0) {
                    if (Sussi.getInstance().getSql().isAlreadyLinkedByID(mentions.get(0).getId()))
                        nick = Sussi.getInstance().getSql().getLinkedNickname(mentions.get(0).getId());
                    else {
                        MessageUtils.sendErrorMessage("Uživatel " + mentions.get(0).getAsMention() + " nemá propojený MC účet.", channel);
                        return;
                    }

                }
            }
        }

        Profile profile;
        if (nick.length() == 0) {
             profile = new Profile(args[0]);
        } else {
            profile = new Profile(nick);
        }

        if(profile.getStatusId() == 404) {
            MessageUtils.sendErrorMessage("Hráč `" + args[0] + "` nebyl nalezen.", channel);
            return;
        }

        if(profile.getStatusId() == 500) {
            MessageUtils.sendErrorMessage("Nepodařilo se provést akci, zkus to zachvilku...", channel);
            return;
        }

        firstPage(sender, message, channel, w, profile, false);
    }

    private void firstPage(User s, Message message, MessageChannel ch, EventWaiter w, Profile profile, boolean generated) {
        if (generated) message.delete().queue();
        Color color = Color.WHITE;
        String role = "Hráč";

        if (Sussi.getInstance().getSql().isAT(profile.getName())) {
            int rank = Sussi.getInstance().getSql().getStalkerStats(profile.getName(), "rank");
            color = getColorByRank(rank);
            role = getRankByID(rank);
        } else if(profile.hasGlobalVIP()) {
            color = profile.getGlobalVIP().getVIPType().getColor();
        }

        EmbedBuilder embedBuilder = MessageUtils.getEmbed(color)
                .setTitle("Informace o hráči: " + profile.getName() + "#" + profile.getDiscriminator () + " (lvl: " + profile.getGlobal_level() + ")")
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
                                "Event pointy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(Sussi.getInstance().getSql().getEventPoints(profile.getName())) + "\n" +
                                "Quest pointy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getAchievementPoints())
                        , true)
                .addField("Hlasování",
                        "Celkem hlasů: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getTotalVotes()) + "\n" +
                                "Měsíční hlasy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getMonthVotes()) + "\n" +
                                "Týdenní hlasy: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getWeekVotes()) + "\n" +
                                "Poslední hlas: " + getDate(profile.getLastVote())
                        , true)
                .addField("Levely",
                        "Survival: " + profile.getSurvival_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getSurvival_experience()) + "XP)" + "\n" +
                                "SkyBlock: " + profile.getSkyblock_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getSkyblock_experience()) + "XP)" + "\n" +
                                "Creative: " + profile.getCreative_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getCreative_experience()) + "XP)" + "\n" +
                                "Prison: " + profile.getPrison_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getPrison_experience()) + "XP)" + "\n" +
                                "Vanilla: " + profile.getVanilla_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getVanilla_experience()) + "XP)" + "\n" +
                                "SkyCloud: " + profile.getSkycloud_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getSkycloud_experience()) + "XP)" + "\n" +
                                "Hardcore Vanilla: " + profile.getHardcore_vanilla_level() + " (" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(profile.getHardcore_vanilla_experience()) + "XP)" + "\n"
                        , true)
                .setFooter("CraftMania.cz Stats")
                .setTimestamp(Instant.from(ZonedDateTime.now()));

        if (profile.hasAnyVIP()) {
            embedBuilder.addField("VIP",
                    (profile.hasGlobalVIP() ? "Global: " + StringUtils.capitalize(profile.getGlobalVIP().getGroup()) + " VIP " + (profile.getGlobalVIP().isPermanent() ? "\n" : "(expirace: " + profile.getGlobalVIP().getFormattedDate() + ")\n") : "") +
                            (profile.getHighestVIPs().stream().map(vip -> vip.getServerName() + ": " + StringUtils.capitalize(vip.getGroup()) + " VIP " + (vip.isPermanent() ? "\n" : "(expirace: " + vip.getFormattedDate() + ")\n")).collect(Collectors.joining())),
                    false);
        }

        ch.sendMessage(embedBuilder.build()).queue();

        // Reactions
        /*ch.sendMessage(embedBuilder .build()).queue((Message m) -> {
                    m.addReaction(Constants.BACK).queue();
                    m.addReaction(Constants.NEXT).queue();
                    m.addReaction(Constants.DELETE).queue();
                    w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                        return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.DELETE));
                    }, (MessageReactionAddEvent ev) -> {
                        m.delete().queue();
                        message.delete().queue();
                    }, 60, TimeUnit.SECONDS, null);

                    w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                        return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.NEXT));
                    }, (MessageReactionAddEvent ev) -> {
                        secondPage(s, m, ch, w, profile);
                    }, 60, TimeUnit.SECONDS, null);
        });*/
    }

    @Deprecated
    private void secondPage(User s, Message message, MessageChannel ch, EventWaiter w, Profile profile) {
        Color color = Color.WHITE;

        if (Sussi.getInstance().getSql().isAT(profile.getName())) {
            int rank = Sussi.getInstance().getSql().getStalkerStats(profile.getName(), "rank");
            color = getColorByRank(rank);
        }

        EmbedBuilder embed = MessageUtils.getEmbed(color)
                .setTitle("Informace o hráči: " + profile.getName() + " (lvl:" + profile.getGlobal_level() + ")")
                .setThumbnail("https://mc-heads.net/head/" + profile.getName() + "/128.png")

                .addField("Levely",
                        "Survival: " + profile.getSurvival_level() + " (" + profile.getSurvival_experience() + "XP)" + "\n" +
                                "SkyBlock: " + profile.getSkyblock_level() + " (" + profile.getSkyblock_experience() + "XP)" + "\n" +
                                "Creative: " + profile.getCreative_level() + " (" + profile.getCreative_experience() + "XP)" + "\n" +
                                "Vanilla: " + profile.getVanilla_level() + " (" + profile.getVanilla_experience() + "XP)" + "\n"
                        , false)
                .setFooter("CraftMania.cz Stats 2/2", Sussi.getJda().getSelfUser().getAvatarUrl())
                .setTimestamp(Instant.from(ZonedDateTime.now()));

        if (profile.hasAnyVIP()) {
            embed.addField("VIP",
                    (profile.hasGlobalVIP() ? "Global: " + StringUtils.capitalize(profile.getGlobalVIP().getGroup()) + " VIP " + (profile.getGlobalVIP().isPermanent() ? "\n" : "(expirace: " + profile.getGlobalVIP().getFormattedDate() + ")\n") : "") +
                            (profile.getHighestVIPs().stream().map(vip -> vip.getServerName() + ": " + StringUtils.capitalize(vip.getGroup()) + " VIP " + (vip.isPermanent() ? "\n" : "(expirace: " + vip.getFormattedDate() + ")\n")).collect(Collectors.joining())),
                    false);
        }

        message.editMessage(embed.build()).queue((Message m) -> {
                    w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                        return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.DELETE));
                    }, (MessageReactionAddEvent ev) -> {
                        m.delete().queue();
                        message.delete().queue();
                    }, 60, TimeUnit.SECONDS, null);

                    w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                        return e.getUser().equals(s) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(Constants.BACK));
                    }, (MessageReactionAddEvent ev) -> {
                        firstPage(s, message, ch, w, profile, true);
                    }, 60, TimeUnit.SECONDS, null);
        });
    }

    @Override
    public String getCommand() {
        return "profile";
    }

    @Override
    public String getDescription() {
        return "Vypíše informace o hráči";
    }

    @Override
    public String getHelp() {
        return ",profile [NICK] - Informace o hráči";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
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
