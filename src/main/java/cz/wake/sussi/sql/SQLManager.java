package cz.wake.sussi.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public final String getRandomArchiv() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT url FROM sussi_archiv ORDER BY RAND() LIMIT 1;");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("url");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
    }

    public final void insertChnge(final String change) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO sussi_archiv (url) VALUES (?);");
            ps.setString(1, change);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
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

    public final void addWhitelistedUUID(final String uuid, final String description) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO uuid_whitelist (uuid, description) VALUES (?, ?);");
            ps.setString(1, uuid);
            ps.setString(2, description);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void removeWhitelistedUUID(final String uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM uuid_whitelist WHERE uuid = ?;");
            ps.setString(1, uuid);
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

    public final List<WhitelistedUUID> getWhitelistedUUIDs() {
        List<WhitelistedUUID> whitelistedUUIDs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM uuid_whitelist;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                whitelistedUUIDs.add(new WhitelistedUUID(ps.getResultSet().getString("uuid"), ps.getResultSet().getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return whitelistedUUIDs;
    }

    public final void delete(final String change) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM sussi_archiv WHERE id = " + change + ";");
            ps.setString(1, change);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final int countArchiv() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(id) AS total FROM sussi_archiv;");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0;
    }

    public final boolean isOnGGT(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM ggt_players WHERE nick = '" + p + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void addtoWhitelist(final String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO ggt_players (nick) VALUES (?);");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void removeFromWhitelist(final String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM ggt_players WHERE nick = ?;");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final List<String> getPlayersOnWhitelist() {
        List<String> names = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM ggt_players;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                names.add(ps.getResultSet().getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return names;
    }

    public final void updateURLData(final String value) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE ggt_settings SET streamlink = '" + value + "';");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void updateLockData(final String value) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE ggt_settings SET server = '" + value + "';");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final String getServerData() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT server FROM ggt_settings;");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("server");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
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

    public final BlacklistName getBlacklistedPlayer(final String nick) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM blacklisted_players WHERE nick = ?;");
            ps.setString(1, nick);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return new BlacklistName(nick, ps.getResultSet().getString("reason"), ps.getResultSet().getString("banned_by"),
                    ps.getResultSet().getLong("time_start"), ps.getResultSet().getLong("time_end"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final List<String> getPlayersInBlacklist() {
        List<String> names = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM blacklisted_players;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                names.add(ps.getResultSet().getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return names;
    }

    public final boolean hasActiveReward(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM vybery_dotaznik WHERE nick = '" + p + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void addToRewardList(final String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO vybery_dotaznik (nick) VALUES (?);");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void removeFromRewardList(final String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("DELETE FROM vybery_dotaznik WHERE nick = ?;");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final List<String> getPlayersOnRewardList() {
        List<String> names = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT nick FROM vybery_dotaznik;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                names.add(ps.getResultSet().getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return names;
    }

    public final RewardPlayer getRewardPlayer(final String nick) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM vybery_dotaznik WHERE nick = ?;");
            ps.setString(1, nick);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return new RewardPlayer(nick, ps.getResultSet().getBoolean("vybrano"), ps.getResultSet().getString("server"),
                        ps.getResultSet().getLong("time"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public final void updateMaintenance(final String server, final int value) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("UPDATE stav_survival_server SET udrzba = ? WHERE `nazev` = ?;");
            ps.setInt(1, value);
            ps.setString(2, server);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final boolean isMaintenance(final String server) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT udrzba FROM stav_survival_server WHERE nazev=?");
            ps.setString(1, server);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                int value = result.getInt("udrzba");
                result.close();
                if (value == 1) {
                    return true;
                }
                else {
                    return false;
                }
            } else {
                result.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final boolean isExistServer(final String server) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM stav_survival_server WHERE nazev = '" + server + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
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

    public final boolean isAlreadyLinked(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_discordconnections WHERE userid = '" + p + "' AND nickname != '-1';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public String getLinkedNickname(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_discordconnections WHERE userid = ? AND nickname != '-1';");
            ps.setString(1, p);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("nickname");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
    }

    public final boolean hasConnection(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_discordconnections WHERE userid = '" + p + "' AND expire > " + System.currentTimeMillis() + ";");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public ConnectTask getActiveConnectionTask(String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_discordconnections WHERE userid = '" + p + "' AND expire > " + System.currentTimeMillis() + ";");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                System.out.print(ps.getResultSet().toString());
                return new ConnectTask(p, ps.getResultSet().getString("code"), ps.getResultSet().getLong("expire"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            pool.close(conn, ps, null);
        }
        return null;
    }

    public ConnectTask createConnectionTask(String p, String code) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("INSERT INTO player_discordconnections (userid, code, expire, nickname) VALUES (?, ?, ?, ?);");
            ps.setString(1, p);
            ps.setString(2, code);
            ps.setLong(3, System.currentTimeMillis() + 15 * 60 * 1000); //15m
            ps.setString(4, "-1");
            ps.executeUpdate();
            return new ConnectTask(p, code, System.currentTimeMillis() + 15 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            pool.close(conn, ps, null);
        }
    }
}
