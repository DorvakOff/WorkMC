package fr.dorvak.workmc.mod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.dorvak.betterjda.lite.utils.logging.BLogger;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.database.core.DbConnection;
import net.dv8tion.jda.core.managers.GuildController;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class BanManager implements Runnable {
	
	private WorkMc main;
	private DbConnection connection;
	private Connection sqlConnection;
	private BLogger logger;
	private HashMap<String, Instant> banneds;
	
	public BanManager(WorkMc main) {
		this.main = main;
		this.connection = this.main.getDatabaseManager().getMainConnection();
		this.logger = this.main.getLogger();
		
		try {
			this.sqlConnection = connection.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		banneds = getBannedsAndTime();
	}
	
	public Instant getBanTime(String id) {
		int time = 0;
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT ban_days FROM BotUsers WHERE id = ?");
			preparedStatement.setString(1, id);
			ResultSet result = preparedStatement.executeQuery();
			time = result.getInt("ban_days");
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
		return Instant.ofEpochMilli(time);
	}
	
	public HashMap<String, Instant> getBannedsAndTime(){
		HashMap<String, Instant> banneds = new HashMap<>();
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT * FROM BotUsers WHERE ban_days IS NOT NULL");
			ResultSet result = preparedStatement.executeQuery();
			
			while(result.next()) {
				Instant expiration = Instant.ofEpochMilli(result.getLong("ban_days"));
				banneds.put(result.getString("id"), expiration);
			}
			
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
		return banneds;
	}
	
	public void registerBan(String userId, int days) {
		
		Instant exp = Instant.now().plusSeconds(3600*24*days);
		
		
		long expiration = exp.toEpochMilli();
		
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("UPDATE BotUsers SET ban_days = ? WHERE id = ?");
			preparedStatement.setLong(1, expiration);
			preparedStatement.setString(2, userId);
			preparedStatement.execute();
			preparedStatement.close();
			banneds.put(userId, exp);
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
	}
	
	public void removeBan(String userId) {
		try {
			GuildController controller = main.getJDA().getGuildById(WorkMc.getInstance().getGuildIdLong()).getController();
			controller.unban(userId).complete();
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("UPDATE BotUsers SET ban_days = NULL WHERE id = ?");
			preparedStatement.setString(1, userId);
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
	}
	
	public void run() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				long now = Instant.now().toEpochMilli();
				
				for(Entry<String, Instant> bans : banneds.entrySet()) {
					if(bans.getValue().toEpochMilli() < now) {
						String id = bans.getKey();
						removeBan(id);
						banneds.remove(id);
					}
				}
				
			}
		}, 10, 15, TimeUnit.SECONDS);
	}
}
