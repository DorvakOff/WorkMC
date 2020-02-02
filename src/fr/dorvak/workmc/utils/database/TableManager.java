package fr.dorvak.workmc.utils.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.dorvak.betterjda.lite.utils.logging.BLogger;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.database.core.DbConnection;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class TableManager {
	
	private WorkMc main;
	private DbConnection connection;
	private Connection sqlConnection;
	private BLogger logger;
	
	public TableManager(WorkMc main) {
		this.main = main;
		this.connection = this.main.getDatabaseManager().getMainConnection();
		this.logger = this.main.getLogger();
		
		try {
			this.sqlConnection = connection.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		createUsersTable();
		logger.sendConsoleInfo("All tables ready. Database setup complete !");
	}
	
	private boolean tableExist(String tableName) {
		
		ResultSet tables = null;
		boolean exist = false;
		try {
			DatabaseMetaData dmd = sqlConnection.getMetaData();
			tables = dmd.getTables(sqlConnection.getCatalog(), null, tableName, null);
			exist = tables.next();
			tables.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exist;
	}
	
	public void createUsersTable() {
		
		logger.sendConsoleInfo("Checking the tables...");
		
		if(tableExist("BotLevels")) {
			logger.sendConsoleInfo("The table 'BotLevels' was found.");
		} else {
		
			logger.sendConsoleInfo("Creating 'BotLevels' table...");
			
			try {
				PreparedStatement ps = sqlConnection.prepareStatement("CREATE TABLE BotLevels("
					+ "id VARCHAR(20) UNIQUE NOT NULL, "
					+ "level INT NOT NULL,"
					+ "total_xp INT NOT NULL)");
				ps.execute();
				ps.close();
						
				logger.sendConsoleSuccess("user table successfully created.");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.sendConsoleError("An error occured while creating the table !");
			}
		}
		
		if(tableExist("BotUsers")) {
			logger.sendConsoleInfo("The table 'BotUsers' was found.");
		} else {
		
			logger.sendConsoleInfo("Creating user table...");
			
			try {
				PreparedStatement ps = sqlConnection.prepareStatement("CREATE TABLE BotUsers("
					+ "id VARCHAR(20) UNIQUE NOT NULL, "
					+ "mute_minutes BIGINT NULL,"
					+ "ban_days BIGINT NULL)");
				ps.execute();
				ps.close();
						
				logger.sendConsoleSuccess("user table successfully created.");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.sendConsoleError("An error occured while creating the table !");
			}
		}
		
		if(tableExist("WarnList")) {
			logger.sendConsoleInfo("The table 'WarnList' was found.");
		} else {
			logger.sendConsoleInfo("Creating WarnList table...");
			
			try {
				PreparedStatement ps = sqlConnection.prepareStatement("CREATE TABLE WarnList("
					+ "id VARCHAR(20) NOT NULL, "
					+ "reason TEXT NOT NULL)");
				ps.execute();
				ps.close();
						
				logger.sendConsoleSuccess("WarnList table successfully created.");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.sendConsoleError("An error occured while creating the table !");
			}
		}
	}
}
