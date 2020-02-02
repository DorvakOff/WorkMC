package fr.dorvak.workmc.utils.database.core;

import java.sql.SQLException;

import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.Constants;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class DatabaseManager {
	
	private DbConnection mainConnection;
	
	public DatabaseManager(WorkMc main) {
		this.mainConnection = new DbConnection(new DbCredentials(Constants.DATABASE_ADDRESS, "workmc", Constants.DATABASE_PASSWORD, "workmc", 3306));
	}
	
	public void close() {
		try {
			mainConnection.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public DbConnection getMainConnection() {
		return mainConnection;
	}
	
}
