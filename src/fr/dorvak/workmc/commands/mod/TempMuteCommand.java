package fr.dorvak.workmc.commands.mod;

import java.awt.Color;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.CommandSenderType;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
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

public class TempMuteCommand extends CommandListener {

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
		
		String reason = message.getContentDisplay().replace(WorkMc.getInstance().getPrefixOnDiscord() + "tempmute ", "").replace("@" + target.getEffectiveName() + " ", "");
		
		int hours = 0;
		
		try {
			hours = Integer.parseInt(reason.split(" ")[0]);
		} catch (Exception e) {
			command.getMessenger().commandInvalidArgs(command);
			return CommandResult.INVALID_ARGS;
		}
				
		WorkMc.getInstance().getMuteManager().registerMute(target.getUser().getId(), hours);
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.red);
		
		if(reason.replace(" ", "").length() > 1) {
			embed.setDescription("Vous avez mute temporairement " + target.getAsMention() + " pendant " + hours + " heures pour : " + reason);
		} else {
			embed.setDescription("Vous avez mute temporairement " + target.getAsMention() + " pendant " + hours + " heures.");
		}
		
		channel.sendMessage(embed.build()).queue();
		
		DiscordLogger.logCommand(command, args);
		return CommandResult.SUCCESS;
	}
	
	@Override
	public boolean canBypassCommandChannel() {
		return true;
	}
}
