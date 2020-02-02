package fr.dorvak.workmc.commands.mod;

import java.awt.Color;
import java.util.List;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.CommandSenderType;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.mod.WarnManager;
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

public class WarnCommand extends CommandListener {
	
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
		
		String reason = message.getContentDisplay().replace(WorkMc.getInstance().getPrefixOnDiscord() + "warn ", "").replace("@" + target.getEffectiveName(), "");
		
		WarnManager warnManager = WorkMc.getInstance().getWarnManager();
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.red);
		
		if(reason.replace(" ", "").length() > 1) {
			embed.setDescription(target.getAsMention() + " a été warn par " + user.getAsMention() + " pour : " + reason);
			warnManager.registerWarn(target.getUser().getId(), reason);
		} else {
			embed.setDescription(target.getAsMention() + " a été warn par " + user.getAsMention());
			warnManager.registerWarn(target.getUser().getId(), "Aucune raison fournie.");
		}
		
		List<String> warns = warnManager.getWarns(target.getUser().getId());
		
		if(warns.size() >= 3) {
			WorkMc.getInstance().getMuteManager().registerMute(target.getUser().getId(), 5);
			embed.addField("Vous avez été mute", "Vous avez été mute 5h car vous êtes à " + warns.size() + " warns.", false);
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
