package cz.wake.sussi.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.commands.CommandType;
import cz.wake.sussi.commands.ICommand;
import cz.wake.sussi.commands.Rank;
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
        String nick = "";
        if(args.length < 1) {
            if (Sussi.getInstance().getSql().isAlreadyLinked(sender.getId())) {
                nick = Sussi.getInstance().getSql().getLinkedNickname(sender.getId());
            }
            MessageUtils.sendErrorMessage("Špatně zadaný příkaz! Př. `,profile MrWakeCZ`", channel);
            return;
        }
        if (nick == "") nick = args[0];

        boolean hasPlayer = Sussi.getInstance().getSql().hasPlayerHalloweenGame(nick);
        if (!hasPlayer) {
            MessageUtils.sendErrorMessage("Musíš si zahrát Halloween minihru, abys mohl/a zobrazit statistiky!", channel);
            return;
        }

        int best_wave = 0, played_games = 0, total_golds = 0, killed_mobs = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("SELECT * FROM halloween_players WHERE nick = ?;");
            ps.setString(1, nick);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                best_wave = ps.getResultSet().getInt("best_wave");
                played_games = ps.getResultSet().getInt("played_games");
                total_golds = ps.getResultSet().getInt("total_golds");
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
                .addField("Nejlepší vlna", best_wave + ". vlna", true)
                .addField("Odehráno", String.valueOf(played_games), true)
                .addField("Celkem goldů", total_golds + "g", true)
                .addField("Zabito monster", String.valueOf(killed_mobs), true)
                .addBlankField(true).addBlankField(true).build()).queue();


    }

    @Override
    public String getCommand() {
        return "hl";
    }

    @Override
    public String getDescription() {
        return "Statistiky z Halloween Minihry 2019";
    }

    @Override
    public String getHelp() {
        return ",hl [nick]";
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
