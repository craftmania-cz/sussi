package cz.wake.sussi.objects.votes;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.ats.ATS;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.MonthUtils;
import cz.wake.sussi.utils.SussiLogger;
import net.dv8tion.jda.api.entities.TextChannel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class VoteManager implements Job {

    private static Long OZNAMENI_ID = Sussi.getConfig().getOznameniID();

    private List<VotePlayer> evaluate(boolean reset) {
        List<VotePlayer> cache = new ArrayList<>();

        SussiLogger.infoMessage("Evaluating monthly votes...");
        String date = new SimpleDateFormat("MM/yyyy").format(System.currentTimeMillis());

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("create table if not exists archive.`votes_archive_" + date + "`\n" +
                    "(\n" +
                    "    pos int auto_increment,\n" +
                    "    nick varchar(32) not null,\n" +
                    "    uuid varchar(64) not null,\n" +
                    "    month_votes int default 0 null,\n" +
                    "    constraint `votes_archive_03/2020_pk`\n" +
                    "        primary key (pos)\n" +
                    ");");
            ps.executeUpdate();

            ps = conn.prepareStatement("INSERT INTO archive.`votes_archive_" + date + "` (nick, uuid, month_votes)\n" +
                    "(SELECT nick, uuid, month_votes FROM minigames.player_profile ORDER BY month_votes DESC LIMIT 50)");
            ps.executeUpdate();

            ps = conn.prepareStatement("SELECT * FROM archive.`votes_archive_" + date + "`;");
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                cache.add(new VotePlayer(resultSet.getString("nick"), UUID.fromString(resultSet.getString("uuid")), resultSet.getInt("month_votes"), resultSet.getInt("pos")));
            }

            ps = conn.prepareStatement("UPDATE minigames.player_profile SET month_votes = 0;");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();;
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }

        return cache;
    }

    public void resetWeek() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("UPDATE minigames.player_profile SET week_votes = 0;");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();;
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TextChannel channel = Sussi.getJda().getTextChannelById(OZNAMENI_ID);
        assert channel != null;
        List<VotePlayer> cache = evaluate(true);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        String month = new SimpleDateFormat("M").format(calendar.getTime());
        String year = new SimpleDateFormat("yyyy").format(calendar.getTime());
        channel.sendMessage(MessageUtils.getEmbed(Color.decode("#3cab59"))
                .setTitle("Výsledky hlasování: " + MonthUtils.getMonthInCzech(Integer.parseInt(month)) + " " + year)
                .setDescription("Zde jsou výslekdy hlasování za minulý měsíc - " + MonthUtils.getMonthInCzech(Integer.parseInt(month)) + " " + year)
                .addField("1.-5.",
                        cache.stream().limit(5).map(voteplayer -> {
                            return voteplayer.getPosition() + ". **" + voteplayer.getNick() + "** (" + voteplayer.getMonthlyVotes() + " hlasů)";
                        }).collect(Collectors.joining("\n")), true)
                .addField("6.-10.",
                        cache.subList(5, 10).stream().map(votePlayer -> {
                            return votePlayer.getPosition() + ". " + votePlayer.getNick() + " (" + votePlayer.getMonthlyVotes() + " hlasů)";
                        }).collect(Collectors.joining("\n")), true)
                .build()
        ).queue();
    }
}
