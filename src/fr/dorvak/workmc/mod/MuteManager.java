package fr.dorvak.workmc.mod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import fr.dorvak.betterjda.lite.utils.logging.BLogger;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.Constants;
import fr.dorvak.workmc.utils.DiscordLogger;
import fr.dorvak.workmc.utils.DiscordLogger.LogAction;
import fr.dorvak.workmc.utils.MultiThreading;
import fr.dorvak.workmc.utils.database.core.DbConnection;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class MuteManager {
	
	private WorkMc main;
	private DbConnection connection;
	private Connection sqlConnection;
	private BLogger logger;
	private HashMap<String, Instant> muteds;
	
	public MuteManager(WorkMc main) {
		this.main = main;
		this.connection = this.main.getDatabaseManager().getMainConnection();
		this.logger = this.main.getLogger();
		
		try {
			this.sqlConnection = connection.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		muteds = getMutedAndTime();
	}
	
	public Instant getBanTime(String id) {
		int time = 0;
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT mute_minutes FROM BotUsers WHERE id = ?");
			preparedStatement.setString(1, id);
			ResultSet result = preparedStatement.executeQuery();
			time = result.getInt("mute_minutes");
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
		return Instant.ofEpochMilli(time);
	}
	
	public HashMap<String, Instant> getMutedAndTime(){
		HashMap<String, Instant> banneds = new HashMap<>();
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT * FROM BotUsers WHERE mute_minutes IS NOT NULL");
			ResultSet result = preparedStatement.executeQuery();
			
			while(result.next()) {
				Instant expiration = Instant.ofEpochMilli(result.getLong("mute_minutes"));
				banneds.put(result.getString("id"), expiration);
			}
			
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
		return banneds;
	}
	
	public void registerMute(String userId, int hours) {
		
		MultiThreading.runAsync(() -> {
			
			Instant exp = Instant.now().plusSeconds(3600*hours);
			long expiration = exp.toEpochMilli();
			
			JDA jda = main.getJDA();
			Member member = jda.getGuildById(Constants.GUILD_ID).getMemberById(userId);
			
			try {
				final PreparedStatement preparedStatement = sqlConnection.prepareStatement("UPDATE BotUsers SET mute_minutes = ? WHERE id = ?");
				preparedStatement.setLong(1, expiration);
				preparedStatement.setString(2, userId);
				preparedStatement.execute();
				preparedStatement.close();
				muteds.put(userId, exp);
			} catch (SQLException e) {
				logger.sendConsoleError(e.getMessage());
			}
			
			for(TextChannel channel : main.getJDA().getGuildById(Constants.GUILD_ID).getTextChannels()) {
				try {
					channel.createPermissionOverride(member).setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).complete();
				} catch (Exception ignored) {continue;}
			}
			
			for(VoiceChannel channel : main.getJDA().getGuildById(Constants.GUILD_ID).getVoiceChannels()) {
				try {
					channel.createPermissionOverride(member).setDeny(Permission.VOICE_SPEAK).complete();
				} catch (Exception ignored) {continue;}
			}
		});
	}
	
	public void removeMute(String userId) {
		JDA jda = main.getJDA();
		Member member = jda.getGuildById(Constants.GUILD_ID).getMemberById(userId);
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("UPDATE BotUsers SET mute_minutes = NULL WHERE id = ?");
			preparedStatement.setString(1, userId);
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
		muteds.remove(userId);
		for(TextChannel channel : main.getJDA().getGuildById(Constants.GUILD_ID).getTextChannels()) {
			try {
				channel.getPermissionOverride(member).delete().complete();
			} catch (Exception ignored) {continue;}
		}
		
		for(VoiceChannel channel : main.getJDA().getGuildById(Constants.GUILD_ID).getVoiceChannels()) {
			try {
				channel.getPermissionOverride(member).delete().complete();
			} catch (Exception ignored) {continue;}
		}
	}
	
	public void run() {
		JDA jda = WorkMc.getInstance().getJDA();
		
		MultiThreading.schedule(() -> {
			
			Instant now = Instant.now();
				for(Entry<String, Instant> mutes : muteds.entrySet()) {
					if(mutes.getValue().minusMillis(now.toEpochMilli()).toEpochMilli() <= 0) {
						String id = mutes.getKey();
						removeMute(id);
						DiscordLogger.log(LogAction.AUTO_LOG, "Unmute automatique de l'utilisateur : **" + jda.getUserById(id).getName() + "**.");
					}
				}
				
		}, 15, TimeUnit.SECONDS);
	}
}
