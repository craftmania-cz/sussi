package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
import cz.wake.sussi.utils.Constants;
import cz.wake.sussi.utils.EmoteList;
import cz.wake.sussi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class HalloweenStats implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1){
            channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setDescription("K zobrazení svých stastistik z Halloween minihry použij příkaz **,hs [nick]**").build()).queue();
        } else {
            String nick = args[0];
            int played = 0, wins = 0, fails = 0, killed_mobs = 0, tommy = 0;

            boolean hasPlayer = Sussi.getInstance().getSql().hasPlayerHalloweenGame(nick);
            if (!hasPlayer) {
                MessageUtils.sendErrorMessage("Musíš si zahrát Halloween minihru, abys mohl/a zobrazit statistiky!", channel);
                return;
            }

            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = Sussi.getInstance().getSql().getPool().getConnection();
                ps = conn.prepareStatement("SELECT * FROM halloween_players WHERE nick = ?;");
                ps.setString(1, nick);
                ps.executeQuery();
                if (ps.getResultSet().next()) {
                    played = ps.getResultSet().getInt("played_games");
                    wins = ps.getResultSet().getInt("wins");
                    fails = ps.getResultSet().getInt("fails");
                    killed_mobs = ps.getResultSet().getInt("killed_mobs");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Sussi.getInstance().getSql().getPool().close(conn, ps, null);
            }

            channel.sendMessage(MessageUtils.getEmbed(Color.ORANGE)
                .setTitle("Statistiky pro: " + nick)
                    .setAuthor(EmoteList.JACK_O_LANTERN+ " Halloween Minigame 2018 " + EmoteList.JACK_O_LANTERN)
                .addField("Odehráno", played + " her", true)
                    .addField("Výhry", wins + "", true)
                    .addField("Fails/Úmrtí", fails + "", true)
                    .addField("Zabito monster", killed_mobs + "", true)
                    .addBlankField(true).addBlankField(true).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "hs";
    }

    @Override
    public String getDescription() {
        return "Statistiky z Halloween Minihry 2018";
    }

    @Override
    public String getHelp() {
        return ",hs [nick]";
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
    public String[] getAliases() {
        return new String[0];
    }
}