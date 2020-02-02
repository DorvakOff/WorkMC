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
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.managers.GuildController;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class KickCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		
		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) return CommandResult.COMMAND_NOT_ALLOWED;
		
		JDA jda = command.getMainInstance().getJDA();
		User user = command.getPerformer();
		Member member = jda.getGuildById(Constants.GUILD_ID).getMember(user);
		Message message = command.getMessage();
		TextChannel channel = command.getTextChannel();

		if(!member.hasPermission(Permission.KICK_MEMBERS)) {
			command.getMessenger().permissionDenied(command);
			return CommandResult.MISSING_PERMISSIONS;
		}
		
		
		if(message.getMentionedMembers().size() == 0 || message.getMentionedMembers().size() > 1) {
			command.getMessenger().commandInvalidArgs(command);
			return CommandResult.INVALID_ARGS;
		}
		
		Member target = message.getMentionedMembers().get(0);
		
		String reason = message.getContentDisplay().replace(WorkMc.getInstance().getPrefixOnDiscord() + "kick ", "").replace("@" + target.getEffectiveName(), "");
		
		EmbedBuilder kicked = EmbedHelper.getBasicEmbed();
		kicked.setColor(Color.RED);
		kicked.setTitle("Vous avez �t� expuls� de WorkMc");
		
		if(reason.replace(" ", "").length() > 1) {
			kicked.setDescription("Vous avez �t� expuls� pour : " + reason);
		}
		
		if(!target.getUser().isBot()) {
			if(!target.getUser().hasPrivateChannel()) target.getUser().openPrivateChannel().complete();
			((UserImpl) target.getUser()).getPrivateChannel().sendMessage(kicked.build()).queue();
		}
		
		GuildController controller = jda.getGuildById(WorkMc.getInstance().getGuildIdLong()).getController();
		
		try	{
			if(reason.replace(" ", "").length() > 1) {
				controller.kick(target, reason).queue();
			} else {
				controller.kick(target).queue();
			}
		} catch (HierarchyException e) {
			EmbedBuilder embed = EmbedHelper.getBasicEmbed();
			embed.setColor(Color.RED);
			embed.setTitle("Oups, quelque chose a rat� !");
			embed.setDescription("> Je n'ai pas la permission pour expulser ce membre !");
			
			channel.sendMessage(embed.build()).queue();
			return CommandResult.COMMAND_NOT_ALLOWED;
		}
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.red);
		
		if(reason.replace(" ", "").length() > 1) {
			embed.setDescription("Vous avez expuls� " + target.getAsMention() + " pour : " + reason);
		} else {
			embed.setDescription("Vous avez expuls� " + target.getAsMention());
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
