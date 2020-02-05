package fr.dorvak.workmc.utils;

import java.awt.Color;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class DiscordLogger {
	
	private static TextChannel logChannel;
	private WorkMc main;
	
	public DiscordLogger(WorkMc main) {
		this.main = main;
		logChannel = this.main.getJDA().getTextChannelById(Constants.LOG_CHANNEL_ID);
	}
	
	public static void logCommand(Command command, String[] args) {
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.red);
		embed.setTitle("Log de commande :");
		embed.setDescription("Utilisation de la commande **" + command.getName() + "** par " + command.getPerformer().getAsMention() + ".");
		
		StringBuilder sb = new StringBuilder();
		
		for(String a : args) {
			sb.append(a + " ");
		}
		
		embed.addField("Commande complète :", "> " + WorkMc.getInstance().getPrefixOnDiscord() + command.getName() + " " + sb.toString(), false);
		logChannel.sendMessage(embed.build()).queue();
	}

	public static void log(LogAction action, String message) {
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.red);
		embed.setTitle(action.getDisplay());
		
		embed.addField("", "> " + message, false);
		logChannel.sendMessage(embed.build()).queue();
	}
	
	public static enum LogAction {
		AUTO_LOG("Action automatique"),
		INFO_LOG("Log d'information"),
		;
		
		private String display;
		
		LogAction(String display){
			this.display = display;
		}
		
		public String getDisplay() {
			return display + " :";
		}
	}

}
