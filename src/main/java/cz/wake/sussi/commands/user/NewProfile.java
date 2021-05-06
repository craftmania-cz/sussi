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
        channel.sendMessage(MessageUtils.getEmbed(Constants.DARK_GRAY).setDescription("Proběhla změna příkazu, nyní používej `/profile [nick] [@user]`").build()).queue();
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
