package net.quillcraft.core.data.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.quillcraft.core.QuillCraftCore;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseAccess {

    private static final String JDBC_DRIVER = "relocated.private.com.mysql.jdbc.Driver";
    private final DatabaseCredentials credentials;
    private final Logger logger = QuillCraftCore.getInstance().getLogger();
    private HikariDataSource hikariDataSource;

    public DatabaseAccess(DatabaseCredentials credentials) {
        this.credentials = credentials;
    }

    private void setupHikariCP() {
        final HikariConfig hikariConfig = new HikariConfig();
        try {
            hikariConfig.setDriverClassName(Class.forName(JDBC_DRIVER).getName());
        } catch (ClassNotFoundException exception) {
            logger.warning("Default JDBC driver is used");
        }
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl(credentials.getAdress());
        hikariConfig.setUsername(credentials.getUser());
        hikariConfig.setPassword(credentials.getPass());
        hikariConfig.setMaxLifetime(600000L); //10min
        //hikariConfig.setIdleTimeout(300000L); //5min
        hikariConfig.setLeakDetectionThreshold(300000L); //5min
        hikariConfig.setConnectionTimeout(10000L); //10sec

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public void initPool() {
        setupHikariCP();
        logger.info("JDBC init pool");
    }

    public void closePool() {
        hikariDataSource.close();
    }

    public Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) {
            logger.warning("Not connected to Database");
            initPool();
        }
        return this.hikariDataSource.getConnection();
    }

}
