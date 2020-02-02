package fr.dorvak.workmc.listeners;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.utils.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class GuildMemberListener extends ListenerAdapter {
	
	private List<Color> colors;
	
	public GuildMemberListener() {
		colors = new ArrayList<>();
		createColors();
		Collections.shuffle(colors);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		JDA jda = event.getJDA();
		GuildController controller = jda.getGuildById(Constants.GUILD_ID).getController();
		Member member = event.getMember();
		controller.addSingleRoleToMember(member, jda.getRoleById(Constants.BASIC_ROLE_ID)).queue();
		
		TextChannel channel = jda.getTextChannelById(Constants.WELCOME_CHANNEL_ID);
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		
		embed.setTitle(":wrench: Bienvenue à " + member.getEffectiveName() + " sur WorkMC !");
		embed.setDescription("> Nous sommes maintenant " + jda.getGuildById(Constants.GUILD_ID).getMembers().size() + " membres sur le discord !");
		embed.setColor(randomColor());
		embed.setThumbnail(member.getUser().getAvatarUrl());
		
		channel.sendMessage(embed.build()).queue();
		
		
		super.onGuildMemberJoin(event);
	}
	
	private void createColors() {
		rgb(9, 132, 227);
		rgb(108, 92, 231);
		rgb(243, 156, 18);
		rgb(46, 204, 113);
		rgb(41, 128, 185);
		rgb(192, 57, 43);
		rgb(26, 188, 156);
		rgb(186, 220, 88);
		rgb(34, 166, 179);
		rgb(72, 52, 212);
	}
	
	public Color randomColor() {
		return colors.get(new Random().nextInt(colors.size() - 1));
	}

	private void rgb(int r, int g, int b) {
		colors.add(new Color(r, g, b));
	}
}
