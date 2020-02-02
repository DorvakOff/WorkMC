package fr.dorvak.workmc.custom;

import java.awt.Color;
import java.util.TimerTask;

import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class DisponibilityChecker extends TimerTask {

	private WorkMc main;
	private JDA jda;
	
	public DisponibilityChecker(WorkMc main) {
		this.main = main;
		this.jda = this.main.getJDA();
		run();
	}
	
	@Override
	public void run() {
		TextChannel channel = jda.getTextChannelById(Constants.DISPONIBILITY_CHANNEL_ID);
		
		try {
			long id = channel.getLatestMessageIdLong();
			Message msg = channel.getMessageById(id).complete();
			msg.editMessage(getEmbed()).complete();
		} catch (Exception ignored) {
			channel.sendMessage(getEmbed()).queue();
		}
	}
	
	public MessageEmbed getEmbed() {
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.GREEN);
		embed.setTitle("Voici la liste des vendeurs de services disponible :");
		embed.setDescription("Cliquez sur la mention pour obtenir tous les grades du membre concerné ! \n (actualisation toutes les 5 minutes)");
		embed.addBlankField(false);
		
		StringBuilder javaDevs = new StringBuilder();
		StringBuilder webDevs = new StringBuilder();
		StringBuilder communityManager = new StringBuilder();
		StringBuilder gfx = new StringBuilder();
		StringBuilder builder = new StringBuilder();
		StringBuilder youtubeur = new StringBuilder();
		StringBuilder trailerMaker = new StringBuilder();
		StringBuilder modelisator = new StringBuilder();
		StringBuilder gameDesigner = new StringBuilder();
		StringBuilder redacteur = new StringBuilder();
		StringBuilder sysAdmin = new StringBuilder();
		
		for(Member m : jda.getGuildById(Constants.GUILD_ID).getMembersWithRoles(jda.getRoleById(659090868120059914L))) {
			if(m.getRoles().contains(jda.getRoleById(673273567625871361L))) {
				javaDevs.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673273669635538988L))) {
				webDevs.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673274108410069013L))) {
				communityManager.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673274361066553384L))) {
				sysAdmin.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673277191911374853L))) {
				builder.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673274257249402923L))) {
				redacteur.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673274686834212895L))) {
				gfx.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673277361352998954L))) {
				modelisator.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673282483042910208L))) {
				youtubeur.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673274758607011851L))) {
				trailerMaker.append("\n > " + m.getAsMention());
			}
			if(m.getRoles().contains(jda.getRoleById(673281291600396288L))) {
				gameDesigner.append("\n > " + m.getAsMention());
			}
		}
		
		if(javaDevs.length() != 0) embed.addField("Développeurs Java :", javaDevs.toString(), false);
		if(webDevs.length() != 0) embed.addField("Développeurs Web :", webDevs.toString(), false);
		if(communityManager.length() != 0) embed.addField("Community Managers :", communityManager.toString(), false);
		if(gfx.length() != 0) embed.addField("Graphistes :", gfx.toString(), false);
		if(builder.length() != 0) embed.addField("Builders :", builder.toString(), false);
		if(youtubeur.length() != 0) embed.addField("Monteurs :", youtubeur.toString(), false);
		if(trailerMaker.length() != 0) embed.addField("Trailer Makers :", trailerMaker.toString(), false);
		if(modelisator.length() != 0) embed.addField("Modélisateurs :", modelisator.toString(), false);
		if(gameDesigner.length() != 0) embed.addField("Game designers :", gameDesigner.toString(), false);
		if(redacteur.length() != 0) embed.addField("Rédacteurs :", redacteur.toString(), false);
		if(sysAdmin.length() != 0) embed.addField("SysAdmins :", sysAdmin.toString(), false);
		
		return embed.build();
	}

}
