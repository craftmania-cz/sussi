package cz.wake.sussi.runnable;

import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.votes.RewardMonthVotePlayer;
import cz.wake.sussi.objects.votes.VotePlayer;
import cz.wake.sussi.utils.*;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import okhttp3.*;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VoteResetTask implements Job {

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

            ps = conn.prepareStatement("UPDATE minigames.player_profile SET month_votes = 0, month_discord_voice_activity = 0, lobby_bonus_claimed_monthly_vip = 0, lobby_bonus_claimed_discord_voice_activity_1h = 0, lobby_bonus_claimed_discord_voice_activity_10h = 0, lobby_bonus_claimed_discord_voice_activity_24h = 0;");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sussi.getInstance().getSql().getPool().close(conn, ps, null);
        }

        return cache;
    }

    private List<RewardMonthVotePlayer> sendRewards(List<VotePlayer> votePlayerList) {
        List<RewardMonthVotePlayer> rewardVotePlayerList = new ArrayList<>();
        for (VotePlayer voteplayer : votePlayerList) {
            SussiLogger.infoMessage("Sending month vote reward for player " + voteplayer.getNick());
            boolean linkedDiscord;

            int statusId;
            JSONObject cmApiJSON;
            try {
                OkHttpClient caller = new OkHttpClient();
                Request request = (new Request.Builder()).url("https://api.craftmania.cz/player/uuid/" + voteplayer.getUuid()).build();
                Response response = caller.newCall(request).execute();
                cmApiJSON = new JSONObject(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
                SussiLogger.fatalMessage("Internal error when retrieving data from CraftMania api!");
                statusId = 500;
                break;
            }
            statusId = cmApiJSON.getInt("status");
            if (statusId != 200) {
                SussiLogger.fatalMessage("Internal error when retrieving data from CraftMania api!");
                break;
            }
            String discordID = cmApiJSON.getJSONObject("data").getJSONObject("discord").isNull("id") ? null : cmApiJSON.getJSONObject("data").getJSONObject("discord").getString("id");

            linkedDiscord = discordID != null;

            String rewardCode = RewardMonthVoteUtils.getRewardCode(RewardMonthVoteUtils.getAmount(voteplayer.getPosition()));

            if (linkedDiscord) {
                RestAction<User> action = Sussi.getJda().retrieveUserById(discordID);
                action.submit()
                        .thenCompose((user) -> user.openPrivateChannel().submit())
                        .thenCompose((channel) -> channel.sendMessage(MessageUtils.getEmbed(Color.decode("#3cab59"))
                                // Zpráva pro obdržitele odměny za hlasování
                                .setDescription("Jako odměnu za hlasování získáváš dárkový poukaz v hodnotě **" + RewardMonthVoteUtils.getAmount(voteplayer.getPosition()) + "** CZK.\nKód: ||" + rewardCode + "|| (klikni pro zobrazení)")
                                .build()).submit())
                        .whenComplete((v, error) -> {
                            if (error != null) {
                                rewardVotePlayerList.add(new RewardMonthVotePlayer(voteplayer.getNick(), voteplayer.getUuid(), true, discordID, false, voteplayer.getPosition(), rewardCode));
                                SussiLogger.dangerMessage("Vote month reward was not sent to player " + voteplayer.getNick());
                            } else {
                                rewardVotePlayerList.add(new RewardMonthVotePlayer(voteplayer.getNick(), voteplayer.getUuid(), true, discordID, true, voteplayer.getPosition(), rewardCode));
                                SussiLogger.greatMessage("Vote month reward was successfully sent to player " + voteplayer.getNick());
                            }
                        });
            } else {
                rewardVotePlayerList.add(new RewardMonthVotePlayer(voteplayer.getNick(), voteplayer.getUuid(), false, null, false, voteplayer.getPosition(), rewardCode));
                SussiLogger.dangerMessage("Winner of vote month reward " + voteplayer.getNick() + " Minecraft account is not linked with Discord.");

            }
        }
        return rewardVotePlayerList;
    }


    private void sendRewardAnnounce(String month, String year, List<RewardMonthVotePlayer> rewardVotePlayerList) {
        SussiLogger.infoMessage("Sending an announce to the bot owner about the sending of monthly rewards for voting.");
        Collections.sort(rewardVotePlayerList, Comparator.comparing(RewardMonthVotePlayer::getPosition));
        Sussi.getJda().getUserById(Sussi.getConfig().getOwnerID())
                .openPrivateChannel()
                .complete()
                .sendMessage(MessageUtils.getEmbed(Color.decode("#3cab59"))
                        .setTitle("Odměny za hlasování: " + MonthUtils.getMonthInCzech(Integer.parseInt(month)) + " " + year)
                        .setDescription("Zde je přehled ohledně posílání a generace odměn - " + MonthUtils.getMonthInCzech(Integer.parseInt(month)) + " " + year)
                        .addField("1.-5.",
                                rewardVotePlayerList.stream().map(rewardMonthVotePlayer -> {
                                    // Vyhodnocování statusu odměny
                                    String status;
                                    if (rewardMonthVotePlayer.isDelivered()) {
                                        status = "✅ Doručeno";
                                    } else if (rewardMonthVotePlayer.isLinkedDiscord() && !rewardMonthVotePlayer.isDelivered()) {
                                        status = "❌ Nedoručeno (chyba při doručování)";
                                    } else if (!rewardMonthVotePlayer.isLinkedDiscord()) {
                                        status = "❌ Nedoručeno (nemá propojený Discord)";
                                    } else {
                                        status = "❌ Nedoručeno (z důvodu chyby)";
                                    }
                                    return rewardMonthVotePlayer.getPosition() + ". `" + rewardMonthVotePlayer.getNick() + "` " + status + "\nOdměna: `" + RewardMonthVoteUtils.getAmount(rewardMonthVotePlayer.getPosition()) + "€` `" + rewardMonthVotePlayer.getRewardCode() + "`";
                                }).collect(Collectors.joining("\n")), true)
                        .build()
                ).queue();
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

        SussiLogger.infoMessage("Starting generating and sending vote month rewards.");
        List<RewardMonthVotePlayer> rewardMonthVotePlayerList = sendRewards(cache.subList(0, 5));

        // Nechci kecat, tohle by se mělo vyřešit lépe
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
            sendRewardAnnounce(month, year, rewardMonthVotePlayerList);
        });
    }
}
