package fr.dorvak.workmc.commands;

import java.awt.Color;
import java.util.List;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class AvailableCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		
		User user = command.getPerformer();
		JDA jda = WorkMc.getInstance().getJDA();
		Member member = jda.getGuildById(Constants.GUILD_ID).getMember(user);
		TextChannel channel = command.getTextChannel();
		Role available, unAvailable;
		
		available = jda.getRoleById(659090868120059914L);
		unAvailable = jda.getRoleById(659091010797830144L);
		
		if(!canUseCommand(member)) {
			command.getMessenger().permissionDenied(command);
			return CommandResult.MISSING_PERMISSIONS;
		}
		
		
		GuildController controller = jda.getGuildById(Constants.GUILD_ID).getController();
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		
		embed.setTitle("Changement de la disponibilité");
		embed.setDescription("> Vous êtes désormais ");
		embed.setColor(Color.CYAN);
		
		if(!member.getRoles().contains(available) && !member.getRoles().contains(unAvailable)) {
			controller.addSingleRoleToMember(member, available).complete();
			embed.appendDescription(available.getAsMention());
			channel.sendMessage(embed.build()).queue();
			return CommandResult.SUCCESS;
		}
		
		if(member.getRoles().contains(available)) {
			controller.removeSingleRoleFromMember(member, available).complete();
			controller.addSingleRoleToMember(member, unAvailable).complete();
			embed.appendDescription(unAvailable.getAsMention());
			channel.sendMessage(embed.build()).queue();
			return CommandResult.SUCCESS;
		}
		
		controller.removeSingleRoleFromMember(member, unAvailable).complete();
		controller.addSingleRoleToMember(member, available).complete();
		embed.appendDescription(available.getAsMention());
		channel.sendMessage(embed.build()).queue();
		return CommandResult.SUCCESS;
	}
	
	public static boolean canUseCommand(Member member) {
		List<Long> allowed = Constants.ALLOWEDS_COMMAND_AVAILABLE;
		for(Role role : member.getRoles()) {
			if(allowed.contains(role.getIdLong())) {
				return true;
			}
		}
		return false;
	}

}
