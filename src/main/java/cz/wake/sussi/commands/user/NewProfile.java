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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NewProfile implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        String nick = "";
        if(args.length < 1) {
            if (!Sussi.getInstance().getSql().isAlreadyLinked(sender.getId())) {
                MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Př. `,profile MrWakeCZ`", channel);
                return;
            } else nick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());

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
        }

        ch.sendMessage(MessageUtils.getEmbed(color)
                .setTitle("Informace o hráči: " + profile.getName() + " (lvl:" + profile.getGlobal_level() + ")")
                .setThumbnail("https://mc-heads.net/head/" + profile.getName() + "/128.png")

                .addField("Globální statistiky",
                        "Registrován: " + getDate(profile.getRegistred()) + "\n" +
                                "Role: " + role + "\n" +
                                "Celkově odehraný čas: " + TimeUtils.formatTime("%d dni, %hh %mm", profile.getPlayedTime(), false) + "\n" +
                                getOnlineString(profile) + "\n" +
                                (profile.isOnline() ? "" : "Naposledy viděn: " + getDate(profile.getLastOnline()))

                        , false)
                .addField("Ekonomika",
                        "CraftCoins: " + profile.getCraftCoins() + "\n" +
                                "CraftTokeny: " + profile.getCraftTokens() + "\n" +
                                "VoteTokeny: " + profile.getVoteTokens() + "\n" +
                                "AchievementPointy: " + profile.getAchievementPoints()
                        , true)
                .addField("Hlasování",
                            "Celkem hlasů: " + profile.getTotalVotes() + "\n" +
                                    "Měsíční hlasy: " + profile.getMonthVotes() + "\n" +
                                    "Týdenní hlasy: " + profile.getWeekVotes() + "\n" +
                                    "Poslední hlas: " + getDate(profile.getLastVote())

                        , true)

                .setFooter("CraftMania.cz Stats 1/2", Sussi.getJda().getSelfUser().getAvatarUrl())
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()).queue((Message m) -> {
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
        });
    }

    private void secondPage(User s, Message message, MessageChannel ch, EventWaiter w, Profile profile) {
        Color color = Color.WHITE;

        if (Sussi.getInstance().getSql().isAT(profile.getName())) {
            int rank = Sussi.getInstance().getSql().getStalkerStats(profile.getName(), "rank");
            color = getColorByRank(rank);
        }

        message.editMessage(MessageUtils.getEmbed(color)
                .setTitle("Informace o hráči: " + profile.getName() + " (lvl:" + profile.getGlobal_level() + ")")
                .setThumbnail("https://mc-heads.net/head/" + profile.getName() + "/128.png")

                .addField("Levely",
                                "Survival: " + profile.getSurvival_level() + " (" + profile.getSurvival_experience() + "XP)" + "\n" +
                                "SkyBlock: " + profile.getSkyblock_level() + " (" + profile.getSkyblock_experience() + "XP)" + "\n" +
                                "Creative: " + profile.getCreative_level() + " (" + profile.getCreative_experience() + "XP)" + "\n" +
                                "Vanilla: " + profile.getVanilla_level() + " (" + profile.getVanilla_experience() + "XP)" + "\n"
                        , false)
                .setFooter("CraftMania.cz Stats 2/2", Sussi.getJda().getSelfUser().getAvatarUrl())
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()).queue((Message m) -> {
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
