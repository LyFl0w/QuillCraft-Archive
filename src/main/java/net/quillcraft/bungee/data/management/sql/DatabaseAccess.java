package net.quillcraft.bungee.data.management.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.quillcraft.bungee.QuillCraftBungee;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseAccess {

    private final DatabaseCredentials credentials;
    private HikariDataSource hikariDataSource;
    private final Logger logger = QuillCraftBungee.getInstance().getLogger();

    public DatabaseAccess(DatabaseCredentials credentials){
        this.credentials = credentials;
    }

    private void setupHikariCP(){
        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl(credentials.toURI());
        hikariConfig.setUsername(credentials.getUser());
        hikariConfig.setPassword(credentials.getPass());
        hikariConfig.setMaxLifetime(600000L); //10min
        hikariConfig.setIdleTimeout(300000L); //5min
        hikariConfig.setLeakDetectionThreshold(300000L); //5min
        hikariConfig.setConnectionTimeout(10000L); //10sec

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public void initPool(){
        setupHikariCP();
        logger.info("JDBC init pool");
    }

    public void closePool(){
        hikariDataSource.close();
    }

    public Connection getConnection() throws SQLException{
        if(this.hikariDataSource == null){
            logger.warning("Not connected to Database");
            initPool();
        }
        return this.hikariDataSource.getConnection();
    }

}
