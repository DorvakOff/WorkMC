package fr.dorvak.workmc.utils.database.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import fr.dorvak.betterjda.lite.utils.logging.BLogger;
import fr.dorvak.workmc.WorkMc;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class DbConnection {
	
	private DbCredentials dbCredentials;
	private Connection connection;
	private BLogger logger;
	
	
	public DbConnection(DbCredentials dbCredentials) {
		logger = WorkMc.getInstance().getLogger();
		this.dbCredentials = dbCredentials;
		this.connect();
	}
	
	private void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(this.dbCredentials.toURI(), this.dbCredentials.getUser(), this.dbCredentials.getPass());
			
				logger.sendConsoleSuccess("Successfully connected to database !");
		} catch (SQLException ignored) {
			logger.sendConsoleError("The Bot has failed to connect to the database, please try again...");
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		
	}
	
	public void closeConnection() throws SQLException {
		if(this.connection != null) {
			if(!this.connection.isClosed()) {
				this.connection.close();
				logger.sendConsoleInfo("Database disconnected.");
			}
		}
	}
	
	public Connection getConnection() throws SQLException {
		if(this.connection != null) {
			if(!this.connection.isClosed()) {
				return this.connection;
			}
		}
		connect();
		return this.connection;
	}
}