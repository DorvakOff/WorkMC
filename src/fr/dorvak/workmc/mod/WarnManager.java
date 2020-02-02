package fr.dorvak.workmc.mod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.dorvak.betterjda.lite.utils.logging.BLogger;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.database.core.DbConnection;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class WarnManager {
	private WorkMc main;
	private DbConnection connection;
	private Connection sqlConnection;
	private BLogger logger;
	
	public WarnManager(WorkMc main) {
		this.main = main;
		this.connection = this.main.getDatabaseManager().getMainConnection();
		this.logger = this.main.getLogger();
		
		try {
			this.sqlConnection = connection.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getWarns(String id) {
		List<String> warns = new ArrayList<>();
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT reason FROM WarnList WHERE id = ?");
			preparedStatement.setString(1, id);
			ResultSet result = preparedStatement.executeQuery();
			
			while(result.next()) {
				warns.add(result.getString("reason"));
			}
			
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
		return warns;
	}
	
	public void registerWarn(String userId, String reason) {
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("INSERT INTO WarnList(id, reason) VALUES (?, ?)");
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, reason);
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
	}
}
