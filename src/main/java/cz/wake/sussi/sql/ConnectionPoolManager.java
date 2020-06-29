package cz.wake.sussi.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.wake.sussi.Sussi;
import cz.wake.sussi.utils.ConfigProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private final Sussi plugin;
    private HikariDataSource dataSource;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;

    public ConnectionPoolManager(Sussi plugin) {
        this.plugin = plugin;
        init();
        setupPool();
    }

    private void init() {
        ConfigProperties config = new ConfigProperties();
        host = config.getHost();
        port = config.getPort();
        database = config.getDbname();
        username = config.getDbuser();
        password = config.getDbpassword();
        minimumConnections = config.getMinConnections();
        maximumConnections = config.getMaxConnections();
        connectionTimeout = config.getTimeout();
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try {
            conn.close();
        } catch (SQLException ignored) {
        }
        if (ps != null) try {
            ps.close();
        } catch (SQLException ignored) {
        }
        if (res != null) try {
            res.close();
        } catch (SQLException ignored) {
        }
    }
}
