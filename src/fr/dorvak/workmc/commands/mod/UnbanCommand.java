package fr.dorvak.workmc.commands.mod;


import java.awt.Color;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.CommandSenderType;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.mod.BanManager;
import fr.dorvak.workmc.utils.Constants;
import fr.dorvak.workmc.utils.DiscordLogger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class UnbanCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		
		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) return CommandResult.COMMAND_NOT_ALLOWED;
		
		JDA jda = command.getMainInstance().getJDA();
		User user = command.getPerformer();
		Member member = jda.getGuildById(Constants.GUILD_ID).getMember(user);
		Message message = command.getMessage();
		TextChannel channel = command.getTextChannel();

		if(!member.hasPermission(Permission.MESSAGE_MANAGE)) {
			command.getMessenger().permissionDenied(command);
			return CommandResult.MISSING_PERMISSIONS;
		}
		
		
		if(message.getMentionedMembers().size() == 0 || message.getMentionedMembers().size() > 1) {
			command.getMessenger().commandInvalidArgs(command);
			return CommandResult.INVALID_ARGS;
		}
		
		Member target = message.getMentionedMembers().get(0);
		
		BanManager banManager = WorkMc.getInstance().getBanManager();
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.red);
		embed.setDescription(target.getAsMention() + " a �t� unban par " + user.getAsMention());
		
		banManager.removeBan(target.getUser().getId());
		channel.sendMessage(embed.build()).queue();
		
		DiscordLogger.logCommand(command, args);
		return CommandResult.SUCCESS;
	}
	
	@Override
	public boolean canBypassCommandChannel() {
		return true;
	}
}