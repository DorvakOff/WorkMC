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

public class TempBanCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		
		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) return CommandResult.COMMAND_NOT_ALLOWED;
		
		JDA jda = command.getMainInstance().getJDA();
		User user = command.getPerformer();
		Member member = jda.getGuildById(Constants.GUILD_ID).getMember(user);
		Message message = command.getMessage();
		TextChannel channel = command.getTextChannel();

		if(!member.hasPermission(Permission.BAN_MEMBERS)) {
			command.getMessenger().permissionDenied(command);
			return CommandResult.MISSING_PERMISSIONS;
		}
		
		
		if(message.getMentionedMembers().size() == 0 || message.getMentionedMembers().size() > 1) {
			command.getMessenger().commandInvalidArgs(command);
			return CommandResult.INVALID_ARGS;
		}
		
		Member target = message.getMentionedMembers().get(0);
		
		String reason = message.getContentDisplay().replace(WorkMc.getInstance().getPrefixOnDiscord() + "tempban ", "").replace("@" + target.getEffectiveName() + " ", "");
		
		int days = 0;
		
		try {
			days = Integer.parseInt(reason.split(" ")[0]);
		} catch (Exception e) {
			command.getMessenger().commandInvalidArgs(command);
			return CommandResult.INVALID_ARGS;
		}
		
		EmbedBuilder banned = EmbedHelper.getBasicEmbed();
		banned.setColor(Color.RED);
		banned.setTitle("Vous avez �t� banni de WorkMC pendant " + days + " jours.");
		
		if(reason.replace(" ", "").length() > 1) {
			banned.setDescription("Vous avez �t� banni pour : " + reason);
		}
		
		if(!target.getUser().isBot()) {
			if(!target.getUser().hasPrivateChannel()) target.getUser().openPrivateChannel().complete();
			((UserImpl) target.getUser()).getPrivateChannel().sendMessage(banned.build()).queue();
		}
		
		
		GuildController controller = jda.getGuildById(WorkMc.getInstance().getGuildIdLong()).getController();
		WorkMc.getInstance().getBanManager().registerBan(target.getUser().getId(), days);
		
		try	{
			if(reason.replace(" ", "").length() > 1) {
				controller.ban(member, 7, reason);
			} else {
				controller.ban(target, 7).queue();
			}
		} catch (HierarchyException e) {
			EmbedBuilder embed = EmbedHelper.getBasicEmbed();
			embed.setColor(Color.RED);
			embed.setTitle("Oups, quelque chose a rat� !");
			embed.setDescription("> Je n'ai pas la permission pour bannir ce membre !");
			
			channel.sendMessage(embed.build()).queue();
			return CommandResult.COMMAND_NOT_ALLOWED;
		}
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.red);
		
		if(reason.replace(" ", "").length() > 1) {
			embed.setDescription("Vous avez banni temporairement " + target.getAsMention() + " pendant " + days + " jours pour : " + reason);
		} else {
			embed.setDescription("Vous avez banni temporairement " + target.getAsMention() + " pendant " + days + " jours.");
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
