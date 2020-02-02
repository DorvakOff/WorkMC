package fr.dorvak.workmc.listeners;

import fr.dorvak.workmc.utils.Constants;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class GuildMemberListener extends ListenerAdapter {
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		JDA jda = event.getJDA();
		GuildController controller = jda.getGuildById(Constants.GUILD_ID).getController();
		controller.addSingleRoleToMember(((GuildMemberJoinEvent) event).getMember(), jda.getGuildById(Constants.GUILD_ID).getRoleById(Constants.BASIC_ROLE_ID)).queue();
		super.onGuildMemberJoin(event);
	}
}
