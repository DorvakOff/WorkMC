package fr.dorvak.workmc.chat;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.betterjda.lite.utils.logging.BLogger;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.Constants;
import fr.dorvak.workmc.utils.database.core.DbConnection;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.UserImpl;

public class LevelsManager {
	
	private WorkMc main;
	private DbConnection connection;
	private Connection sqlConnection;
	private BLogger logger;
	private JDA jda;
	
	public LevelsManager(WorkMc main) {
		this.main = main;
		this.connection = this.main.getDatabaseManager().getMainConnection();
		this.logger = this.main.getLogger();
		this.jda = this.main.getJDA();
		
		try {
			this.sqlConnection = connection.getConnection();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
		
		try {
			jda.getTextChannelById(Constants.PROJECT_CHANNEL_ID).createPermissionOverride(jda.getGuildById(Constants.GUILD_ID).getPublicRole()).setDeny(Permission.MESSAGE_WRITE).complete();
		} catch (Exception ignored) {}
	}

	public boolean hasAccount(String id) {
		boolean hasAccount = false;
		try {
			PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT id FROM BotLevels WHERE id = ?");
			preparedStatement.setString(1, id);
			ResultSet result = preparedStatement.executeQuery();
			hasAccount = result.next();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hasAccount;
	}

	public void createAccount(String id) {
		try {				
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("INSERT INTO BotLevels VALUES (?, ?, ?)");
			preparedStatement.setString(1, id);
			preparedStatement.setInt(2, 1);
			preparedStatement.setInt(3, 1);
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
	}
	
	public int getExp(String id) {
		int i = 0;
		
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT * FROM BotLevels WHERE id = ?");
			preparedStatement.setString(1, id);
			ResultSet result = preparedStatement.executeQuery();
			result.next();
			i = result.getInt("total_xp");
			preparedStatement.close();
		} catch (SQLException e) {}
		return i;
	}
	
	public int getLevel(String id) {
		int i = 0;
		
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT * FROM BotLevels WHERE id = ?");
			preparedStatement.setString(1, id);
			ResultSet result = preparedStatement.executeQuery();
			result.next();
			i = result.getInt("level");
			preparedStatement.close();
		} catch (SQLException e) {}
		return i;
	}

	public void addExp(String id, TextChannel channel) {
		int currentExp = getExp(id);
		int currentLevel = getLevel(id);
		Random r = new Random();
		User user = jda.getUserById(id);
		Member member = jda.getGuildById(Constants.GUILD_ID).getMember(user);
		
		currentExp = currentExp + r.nextInt(10) + 5;
		
		if(calculateExpForNextLevel(currentLevel) < currentExp) {
			currentLevel++;
			currentExp = 0;
			
			
			EmbedBuilder embed = EmbedHelper.getBasicEmbed();
			embed.setColor(Color.GRAY);
			embed.setTitle(jda.getGuildById(Constants.GUILD_ID).getMember(user).getEffectiveName() + " passe au niveau " + currentLevel);
			embed.setDescription(LevelUpMessages.random().replace("%X%", user.getAsMention()).replace("%L%", currentLevel + ""));
			channel.sendMessage(embed.build()).queue();
			saveExpAndLevel(id, currentExp, currentLevel);
			if(currentLevel == 5 && currentExp == 0) {
				giveAccessToProject(id);
			}
		}
		
		if(currentLevel >= 5 && !member.hasPermission(jda.getTextChannelById(Constants.PROJECT_CHANNEL_ID), Permission.MESSAGE_WRITE)) {
			giveAccessToProject(id);
		}
		
		saveExpAndLevel(id, currentExp, currentLevel);
	}

	public void saveExpAndLevel(String id, int exp, int level) {
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("UPDATE BotLevels SET level = ?, total_xp = ? WHERE id = ?");
			preparedStatement.setInt(1, level);
			preparedStatement.setInt(2, exp);
			preparedStatement.setString(3, id);
			preparedStatement.execute();
			preparedStatement.close();
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
	}

	public int calculateExpForNextLevel(int currentLevel) {
		int expForLevel = (int) (6 * currentLevel*currentLevel*currentLevel);
		int i = (int) (6.5 * expForLevel + 7);
		return i;
	}
	
	
	private void giveAccessToProject(String id) {
		User user = jda.getUserById(id);
		Member member = jda.getGuildById(Constants.GUILD_ID).getMember(user);
		TextChannel channel = jda.getTextChannelById(Constants.PROJECT_CHANNEL_ID);
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.GREEN);
		embed.setTitle("Bonne nouvelle !");
		embed.setDescription("Vous avez désormais accès au salon " + channel.getAsMention() + " .");
		if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
		((UserImpl) user).getPrivateChannel().sendMessage(embed.build()).queue();
		
		channel.createPermissionOverride(member).setAllow(Permission.MESSAGE_WRITE).complete();
	}
	
	public HashMap<Integer, UserAndLevel> getBestLevels() {
		HashMap<Integer, UserAndLevel> top = new HashMap<>();
		
		try {
			final PreparedStatement preparedStatement = sqlConnection.prepareStatement("SELECT level, id FROM BotLevels ORDER BY level DESC, total_xp DESC");
			ResultSet result = preparedStatement.executeQuery();
			int i = 1;
			while(result.next()) {
				top.put(i, new UserAndLevel(result.getString("id"), result.getInt("level")));
				i++;
			}
			
		} catch (SQLException e) {
			logger.sendConsoleError(e.getMessage());
		}
		
		return top;
	}
	
}
