package cz.wake.sussi.objects.ats;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.MessageUtils;
import cz.wake.sussi.utils.SussiLogger;
import cz.wake.sussi.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ATSManager implements Job {

    // TODO: Do configu
    public static final String PRIVATE_CHANNEL_ID = Sussi.getConfig().getSecretChannelAtsID();
    public static final String AT_POKEC_ID = Sussi.getConfig().getAtPokecID();

    public Triple<EmbedBuilder, EmbedBuilder, List<ATS>> evaluate(boolean reset) {
        Set<ATS> cache = new HashSet<>();

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Sussi.getInstance().getSql().getPool().getConnection();
            ps = conn.prepareStatement("SELECT nick, min_hours FROM at_table;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                if (ps.getResultSet().getInt("min_hours") > 0) cache.add(new ATS(ps.getResultSet().getString("nick")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }

        SussiLogger.infoMessage("Evaluating ATS...");

        List<ATS> notEvaluated = new ArrayList<>();
        for (ATS ats : cache) {
            boolean evaluated = ats.evaluate();
            if (!evaluated) notEvaluated.add(ats);
        }

        String date = new SimpleDateFormat("MM/yyyy").format(System.currentTimeMillis());

        if (reset) {
            SussiLogger.infoMessage("Resetting ATS...");
            try {
                conn = Sussi.getInstance().getSql().getPool().getConnection();
                ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS archive.`ats_archive_" + date + "` LIKE minigames.at_table;");
                ps.executeUpdate();
                ps = conn.prepareStatement("INSERT INTO archive.`ats_archive_" + date + "` SELECT * FROM minigames.at_table;");
                ps.executeUpdate();
                ps = conn.prepareStatement("UPDATE minigames.at_table SET surv_chat_body = 0, surv_played_time = 0, sky_chat_body = 0, sky_played_time = 0, crea_chat_body = 0, crea_played_time = 0, prison_chat_body = 0, prison_played_time = 0, vanilla_chat_body = 0, vanilla_played_time = 0, minigames_chat_body = 0, minigames_played_time = 0, skycloud_chat_body = 0, skycloud_played_time = 0, build_played_time = 0, events_played_time = 0;");
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Sussi.getInstance().getSql().getPool().close(conn, ps, null);
            }
        }

        int totalCompleted = ((int) cache.stream().filter(ATS::isComplete).count());
        int incomplete = ((int) cache.stream().filter(ats -> !ats.isComplete()).count());
        double percentage = ((double) totalCompleted / cache.size()) * 100.00;
        long totalPlayedTime = cache.stream().mapToInt(ATS::getTotalTime).sum();
        int getTotalChatPoints = cache.stream().mapToInt(ATS::getTotalActivity).sum();
        Set<ATS> highestPlayTime5 = cache.stream().sorted(Comparator.comparingInt(ATS::getTotalTime).reversed()).limit(5).collect(Collectors.toSet());
        Set<ATS> lowestPlayTime5 = cache.stream().sorted(Comparator.comparingInt(ATS::getTotalTime)).limit(5).collect(Collectors.toSet());
        Set<ATS> compeleted = cache.stream().filter(ATS::isComplete).sorted(Comparator.comparingInt(ATS::getTotalTime).reversed()).collect(Collectors.toSet());
        Set<ATS> incompleted = cache.stream().filter(ats -> !ats.isComplete()).sorted(Comparator.comparingInt(ATS::getTotalTime).reversed()).collect(Collectors.toSet());

        return Triple.of(
                new EmbedBuilder()
                        .setColor((totalCompleted > incomplete) ? Color.decode("#38b559") : Color.RED)
                        .setTitle("Shrnutí vyhodnocení ATS - " + date)
                        .addField("Splnění ATS", "**" + totalCompleted + "** splněných z **" + cache.size() + "** (" + String.format("%.2f", percentage) + "%)", true)
                        .addField("Celkový odehraný čas", TimeUtils.formatTime("%d dni, %hh %mm", totalPlayedTime, false), true)
                        .addField("Celková aktivita", getTotalChatPoints + " bodů", true)
                        .addField("Nejvíce odehráli \uD83E\uDC13", getPlayTimes(highestPlayTime5), true)
                        .addField("Nejméně odehráli \uD83E\uDC11", getPlayTimes(lowestPlayTime5), true)
                        .setFooter("v1.0"),
                new EmbedBuilder()
                        .setColor((totalCompleted > incomplete) ? Color.decode("#38b559") : Color.RED)
                        .setTitle("Plné shrnutí vyhodnocení ATS - " + date)
                        .addField("Splnění ATS", "**" + totalCompleted + "** splněných z **" + cache.size() + "** (" + String.format("%.2f", percentage) + "%)", true)
                        .addField("Celkový odehraný čas", TimeUtils.formatTime("%d dni, %hh %mm", totalPlayedTime, false), true)
                        .addField("Celková aktivita", getTotalChatPoints + " bodů", true)
                        .addField("Tabulka splněných ATS \uD83E\uDC13", getPlayTimes(compeleted), true)
                        .addField("Tabulka nesplněných ATS \uD83E\uDC13", getPlayTimes(incompleted), true)
                , notEvaluated);
    }

    public String getPlayTimes(Set<ATS> atsList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ATS ats : atsList) {
            stringBuilder.append((ats.getDiscordID() == null ? ats.getName() : "<@" + ats.getDiscordID() + ">") + " (" + TimeUtils.formatTime("%d dni, %hh %mm", ats.getTotalTime(), false) + ")\n");
        }
        return stringBuilder.toString();
    }
    // ❌❌❌	\u274C
    public String getPlayTimesWithIndicator(List<ATS> atsList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ATS ats : atsList) {
            stringBuilder.append((ats.getDiscordID() == null ? ats.getName() : "<@" + ats.getDiscordID() + ">") + " (" + TimeUtils.formatTime("%d dni, %hh %mm", ats.getTotalTime(), false) + ") " + (ats.isComplete() ? "" : "\u274C") + "\n");
        }
        return stringBuilder.toString();
    }

    public boolean isInATS(@NotNull String nick) {
        return Sussi.getInstance().getSql().isAT(nick);
    }

    /**
     * Check firstly if user is in ATS by using {@link ATSManager#isInATS(String)}
     *
     * @param nick Player's nick
     * @return ATS or null if player is not in ATS
     */
    @Nullable
    public ATS getATS(@NotNull String nick) {
        if (isInATS(nick)) {
            return new ATS(nick);
        }
        return null;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TextChannel channel = Sussi.getJda().getTextChannelById(AT_POKEC_ID);
        assert channel != null;
        channel.sendMessage(MessageUtils.getEmbed()
                .setTitle("Vyhodnocení ATS - " + (new SimpleDateFormat("MM/yyyy").format(System.currentTimeMillis())))
                .setDescription("Vyhodnocuji ATS...").build()).queue(msg -> {
            Triple<EmbedBuilder, EmbedBuilder, List<ATS>> pair = this.evaluate(true);
            msg.editMessage(pair.getLeft().build()).queue();
            if (!pair.getRight().isEmpty())
                channel.sendMessage(":warning: `" + pair.getRight().stream().map(ATS::getName).collect(Collectors.joining("` `")) + "` se nepodařilo zaslat individuální ATS do DM.").queue();
            MessageChannel secretChannel = Sussi.getJda().getTextChannelById(PRIVATE_CHANNEL_ID);
            if (secretChannel == null) return;
            secretChannel.sendMessage(pair.getMiddle().build()).queue();
        });
    }
}
