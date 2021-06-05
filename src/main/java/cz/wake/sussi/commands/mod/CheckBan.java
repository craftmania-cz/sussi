package cz.wake.sussi.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.objects.LBan;
import cz.wake.sussi.objects.LPlayer;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CheckBan implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {

    }

    @Override
    public String getCommand() {
        return "checkban";
    }

    @Override
    public String getDescription() {
        return "Kontrola, zda má hráč ban.";
    }

    @Override
    public String getHelp() {
        return ",checkban [nick/IP]";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    private String getDate(long time) {
        if(time == -1){
            return "Permanentní";
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
    }

    private String resolveBan(boolean ban){
        if(ban){
            return "Ano";
        }
        return "Ne";
    }

    private String controlIP(String ip, MessageChannel tc){
        if(tc.getId().equals("236749682229903360") || tc.getId().equals("402262554375880705") || tc.getId().equals("484807072060538881") || tc.getId().equals("451785371399749642")) {
            return ip;
        }
        return "Skrytá";
    }


}
