package cz.wake.sussi.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.sussi.Sussi;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
}
