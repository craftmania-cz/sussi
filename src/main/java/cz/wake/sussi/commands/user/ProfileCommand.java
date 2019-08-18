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

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileCommand implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        String nick = "";
        if(args.length < 1) {
            if (Sussi.getInstance().getSql().isAlreadyLinked(sender.getId())) {
                nick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
            }
            MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Př. `,profile MrWakeCZ`", channel);
            return;
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

        EmbedBuilder builder = new EmbedBuilder();

        Color color = Color.WHITE;
        String role = "Hrac";

        // Dočasné zastoupení ranku z SQL
        //TODO: Až bude rank v API, tak předělat
        if (Sussi.getInstance().getSql().isAT(args[0])) {
            int rank = Sussi.getInstance().getSql().getStalkerStats(args[0], "rank");
            color = getColorByRank(rank);
            role = getRankByID(rank);
        }

        builder.setColor(color);
        builder.setTitle("Informace o hráči: " + profile.getName());
        builder.setThumbnail("https://mc-heads.net/head/" + args[0] + "/128.png");
        
        builder.addField("Role", role, true);
        builder.addField("Online", profile.isOnline() ? "Ano" : "Ne", true);
        builder.addField("Zaregistrován", getDate(profile.getRegistred()), true);
        builder.addField("Celkově odehraný čas", TimeUtils.formatTime("%d dni, %hh %mm", profile.getPlayedTime(), false), true);
        builder.addField("Naposledy viděn na", profile.getLastServer(), true);
        builder.addField("CraftCoiny", String.valueOf(profile.getCraftCoins()), true);
        builder.addField("CraftTokeny", String.valueOf(profile.getCraftTokens()), true);
        builder.addField("VoteTokeny", String.valueOf(profile.getVoteTokens()), true);
        builder.addField("Hlasy", String.valueOf(profile.getTotalVotes()), true);
        builder.addField("Hlasy (týden)", String.valueOf(profile.getWeekVotes()), true);
        builder.addField("Hlasy (měsíc)", String.valueOf(profile.getMonthVotes()), true);
        channel.sendMessage(builder.build()).queue();
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
}
