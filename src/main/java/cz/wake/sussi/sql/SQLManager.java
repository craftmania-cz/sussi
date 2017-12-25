package cz.wake.sussi.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.sussi.Sussi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
