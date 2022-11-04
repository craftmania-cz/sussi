package cz.wake.sussi.sql;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.*;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLManager {

    private final Sussi plugin;
    private final ConnectionPoolManager pool;
    private HikariDataSource dataSource;

    public SQLManager(Sussi plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);
    }

    public void onDisable() {
        pool.closePool();
    }

    public ConnectionPoolManager getPool() {
        return pool;
    }

    public final int getStalkerStats(String p, String stats) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT " + stats + " FROM at_table WHERE nick = ?");
            ps.setString(1, p);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt(stats);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public final Long getStalkerStatsTime(String p, String stats) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT " + stats + " FROM at_table WHERE nick = '" + p + "'");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong(stats);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return (long) 0;
    }

    public final void resetATS(String data) {
        ((Runnable) () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE at_table SET " + data + " = '0';");
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        }).run();
    }

    public final boolean isAT(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM at_table WHERE nick = '" + p + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void addWhitelistedIP(final String address, final String description) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO ip_whitelist (address, description) VALUES (?, ?);");
            ps.setString(1, address);
            ps.setString(2, description);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void removeWhitelistedIP(final String address) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM ip_whitelist WHERE address = ?;");
            ps.setString(1, address);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void addWhitelistedNick(final String nick, final String description) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO name_whitelist (nick, description) VALUES (?, ?);");
            ps.setString(1, nick);
            ps.setString(2, description);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void removeWhitelistedName(final String nick) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM name_whitelist WHERE nick = ?;");
            ps.setString(1, nick);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final List<WhitelistedIP> getWhitelistedIPs() {
        List<WhitelistedIP> whitelistedIPS = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM ip_whitelist;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                whitelistedIPS.add(new WhitelistedIP(ps.getResultSet().getString("address"), ps.getResultSet().getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return whitelistedIPS;
    }

    public final List<WhitelistedNick> getWhitelistedNicks() {
        List<WhitelistedNick> whitelistedNicks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM name_whitelist;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                whitelistedNicks.add(new WhitelistedNick(ps.getResultSet().getString("nick"), ps.getResultSet().getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return whitelistedNicks;
    }

    public final LPlayer getPlayerBanlistObject(final String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM bungeecord.litebans_history WHERE name = ?;");
            ps.setString(1, name);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return new LPlayer(name, ps.getResultSet().getString("uuid"), ps.getResultSet().getString("ip"), ps.getResultSet().getString("date"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final LBan getActiveBanObject(final String uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM bungeecord.litebans_bans WHERE uuid = ? AND active = (1);");
            ps.setString(1, uuid);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return new LBan(uuid, ps.getResultSet().getString("reason"), ps.getResultSet().getString("banned_by_name"),
                        ps.getResultSet().getLong("time"), ps.getResultSet().getLong("until"),
                        ps.getResultSet().getBoolean("ipban"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final String getIPFromServerByPlayer(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT ip FROM bungeecord.litebans_history WHERE name = ?;");
            ps.setString(1, player);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("ip");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public String getCraftBungeeConfigValue(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT `value` FROM craftbungee_config WHERE `name` = ?;");
            ps.setString(1, name);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
    }

    public void updateCraftBungeeConfigValue(String name, String value) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE craftbungee_config SET `value`=? WHERE `name`=?;");
            ps.setString(1, value);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    //Discord Connections

    public final boolean isConnectedToMC(final String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_profile WHERE discord_user_id = '" + id + "';");
            //discord_user_id NULL
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final boolean doesConnectionExist(final String c) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_discordconnections WHERE code = '" + c + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final String getConnectionNick(final String c) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM player_discordconnections WHERE code = '" + c + "';");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("nick");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
    }

    public final String getMinecraftNick(final String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM player_profile WHERE discord_user_id = '" + id + "';");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("nick");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
    }

    public final void connectToMC(final String id, final String code) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET discord_user_id = ? WHERE nick = (SELECT nick FROM player_discordconnections WHERE code = ?);");
            ps.setString(1, id);
            ps.setString(2, code);
            ps.executeUpdate();
            ps = conn.prepareStatement("DELETE FROM player_discordconnections WHERE code = ?;");
            ps.setString(1, code);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void disconnectFromMC(final String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET discord_user_id = NULL WHERE discord_user_id = ?;");
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final boolean isAlreadyLinkedByID(String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_profile WHERE discord_user_id = ?");
            ps.setString(1, id);
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    @Nullable
    public final String getLinkedDiscordID(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT discord_user_id FROM player_profile WHERE nick = ?");
            ps.setString(1, p);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("discord_user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public String getLinkedNickname(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM player_profile WHERE discord_user_id = ?;");
            ps.setString(1, p);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("nick");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
    }

    public final boolean existsPlayer(final String nick) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_profile WHERE nick = ?;");
            ps.setString(1, nick);
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return false;
    }

    @Nullable
    public String getPlayerUUID(String nick) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT uuid FROM player_profile WHERE nick = ?;");
            ps.setString(1, nick);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("uuid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }


    public final String getLatestNews() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT `value` FROM craftlobby_settings WHERE `name` = 'important_news';");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
    }

    public final boolean sawLatestNews(final String nick) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT seen_latest_news FROM player_profile WHERE nick = '" + nick + "';");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt("seen_latest_news") == 1 ? true : false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return false;
    }

    public final void updateLatestNews(final String message) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE craftlobby_settings SET `value` = ? WHERE `name` = 'important_news';");
            ps.setString(1, message);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void resetNewsReads() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET `seen_latest_news` = 0");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final boolean hasPlayerHalloweenGame(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM halloween_players WHERE nick = '" + p + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final List<String> getAllowedBlacklistedNames() {
        List<String> allowedNames = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM allowed_blacklisted_names;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                allowedNames.add(ps.getResultSet().getString("nick"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return allowedNames;
    }

    public boolean isATSArchived(String date) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM ?;");
            ps.setString(1, "ats_archive_" + date);
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return false;
    }

    public final void giveBugPoints(String p, int count) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET bug_points = bug_points + ? WHERE nick = ?");
            ps.setInt(1, count);
            ps.setString(2, p);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void createNewPlayerVoice(Long ownerId, Long roomId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO discord_active_voice_rooms(discord_room_id, discord_owner_id) VALUES(?, ?)");
            ps.setString(1, roomId.toString());
            ps.setString(2, ownerId.toString());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
     }

    public final long getPlayerVoiceRoomIdByOwnerId(long ownerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT discord_room_id FROM discord_active_voice_rooms WHERE discord_owner_id = ?");
            ps.setLong(1, ownerId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong("discord_room_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public final long getPlayerVoiceOwnerIdByRoomId(long roomId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT discord_owner_id FROM discord_active_voice_rooms WHERE discord_room_id = ?");
            ps.setLong(1, roomId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong("discord_owner_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public final void deletePlayerVoice(long roomId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM discord_active_voice_rooms WHERE discord_room_id = ?");
            ps.setLong(1, roomId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final Set<String> getLinkedProfiles() {
        Set<String> profiles = new HashSet<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM player_profile WHERE discord_user_id IS NOT null;");
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                profiles.add(resultSet.getString("nick"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return profiles;
    }

    public final void addDiscordVoiceActivity(String userId, Long amount) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET discord_voice_activity = discord_voice_activity + ?, month_discord_voice_activity = month_discord_voice_activity + ? WHERE discord_user_id = ?;");
            ps.setLong(1, amount);
            ps.setLong(2, amount);
            ps.setString(3, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void addDiscordTextActivity(String userId, Long amount) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET discord_text_activity = discord_text_activity + ? WHERE discord_user_id = ?;");
            ps.setLong(1, amount);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void updatePlayerStatus(final String nick, final String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET status = ? WHERE `nick` = ?;");
            ps.setString(1, status);
            ps.setString(2, nick);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void updatePlayerGenderId(final String nick, final int genderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET gender = ? WHERE `nick` = ?;");
            ps.setInt(1, genderId);
            ps.setString(2, nick);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void resetDailyBonus() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET lobby_bonus_streak = 0 WHERE lobby_bonus_claimed_daily = 0");
            ps.executeUpdate();
            ps = conn.prepareStatement("UPDATE player_profile SET lobby_bonus_claimed_daily = 0;");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void updateBooster(final String id, final int booster) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE player_profile SET discord_booster = ? WHERE `discord_user_id` = ?;");
            ps.setInt(1, booster);
            ps.setString(2, id);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final List<String> getDiscordBoosters() {
        List<String> idlist = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT discord_user_id FROM player_profile WHERE discord_booster = 1;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                idlist.add(ps.getResultSet().getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return idlist;
    }


    public final Set<String> getAllLinkedProfiles() {
        Set<String> profiles = new HashSet<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT discord_user_id FROM player_profile WHERE discord_user_id IS NOT null;");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                profiles.add(resultSet.getString("discord_user_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return profiles;
    }

    public final VoiceRoom getVoiceRoom(String userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT discord_user_id, voiceroom_name, voiceroom_limit, voiceroom_locked, voiceroom_bitrate, voiceroom_addedMembers, voiceroom_bannedMembers FROM discord_voice_rooms WHERE discord_user_id = ?");
            ps.setString(1, userId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                Gson gson = new Gson();
                List<String> addedMembers = gson.fromJson(ps.getResultSet().getString("voiceroom_addedMembers"), List.class);
                List<String> bannedMembers = gson.fromJson(ps.getResultSet().getString("voiceroom_bannedMembers"), List.class);
                return new VoiceRoom(ps.getResultSet().getLong("discord_user_id"), ps.getResultSet().getString("voiceroom_name"), ps.getResultSet().getInt("voiceroom_limit"), ps.getResultSet().getBoolean("voiceroom_locked"), ps.getResultSet().getInt("voiceroom_bitrate"), addedMembers, bannedMembers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final void updateVoiceRoomName(String userId, String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE discord_voice_rooms SET voiceroom_name = ? WHERE discord_user_id = ?;");
            ps.setString(1, name);
            ps.setString(2, userId);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void updateVoiceRoomLocked(String userId, Boolean locked) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE discord_voice_rooms SET voiceroom_locked = ? WHERE discord_user_id = ?;");
            ps.setBoolean(1, locked);
            ps.setString(2, userId);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void updateVoiceRoomInt(String userId, String type, Integer value) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE discord_voice_rooms SET voiceroom_" + type + " = ? WHERE discord_user_id = ?;");
            ps.setInt(1, value);
            ps.setString(2, userId);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final List<String> getVoiceRoomMembers(String userId, String type) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT voiceroom_" + type + "Members FROM discord_voice_rooms WHERE discord_user_id = ?;");
            ps.setString(1, userId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                Gson gson = new Gson();
                List<String> members = gson.fromJson(ps.getResultSet().getString("voiceroom_" + type + "Members"), List.class);
                return members;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final void updateVoiceRoomMembers(String userId, String type, List<String> members) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE discord_voice_rooms SET voiceroom_" + type + "Members = ? WHERE discord_user_id = ?;");
            ps.setString(1, members.toString().replaceAll("([\\w.]+)", "\"$1\""));
            ps.setString(2, userId);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final List<String> getAllDiscordMembers() {
        List<String> memberIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT discord_user_id FROM discord_voice_rooms;");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                memberIds.add(resultSet.getString("discord_user_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return memberIds;
    }

    public final void addDiscordMembers(final String userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO discord_voice_rooms (discord_user_id) VALUES (?);");
            ps.setString(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final int getServerLevel(long userId, String serverId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT " + serverId + " FROM player_profile WHERE discord_user_id = ?;");
            ps.setLong(1, userId);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt(serverId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public final void createNotificationForPlayer(
            final String playerUUID,
            final String notificationType,
            final String notificationPriority,
            final String serverScope,
            final String notificationTitle,
            final String notificationBody
        ) {
        Connection conn = null;
        PreparedStatement ps = null;
        long currentTime = System.currentTimeMillis();
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO player_notifications (uuid,type,priority,server,title,message,createdAt) VALUES (?,?,?,?,?,?,?);");
            ps.setString(1, playerUUID);
            ps.setString(2, notificationType);
            ps.setString(3, notificationPriority);
            ps.setString(4, serverScope);
            ps.setString(5, notificationTitle);
            ps.setString(6, notificationBody);
            ps.setLong(7, currentTime);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

}
