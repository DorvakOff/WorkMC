package fr.dorvak.workmc.commands.mod;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class ClearCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		
		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) {
			command.getMainInstance().getLogger().sendConsoleError("Only a discord user can perform this command");
			return CommandResult.COMMAND_NOT_ALLOWED;
		}
		
		JDA jda = WorkMc.getInstance().getJDA();
		User user = command.getPerformer();
		TextChannel channel = command.getTextChannel();
		
		if(jda.getGuildById(Constants.GUILD_ID).getMember(user).hasPermission(Permission.MESSAGE_MANAGE)) {
			EmbedBuilder embed = EmbedHelper.getBasicEmbed();
			if(args.length >= 1) {
				int count = 0;
				try {
					count = Integer.parseInt(args[0]);
				} catch (Exception ignored) {
					command.getMessenger().commandInvalidArgs(command);
					return CommandResult.INVALID_ARGS;
				}
				
				if(count > 99) count = 99;
				if(count < 1) count = 100;
				
				MessageHistory history = new MessageHistory(channel);
				List<Message> msgs;
				msgs = history.retrievePast(count + 1).complete();
				channel.deleteMessages(msgs).queue();
				
				embed.setColor(Color.GREEN);
				embed.setTitle("Et voilà  !");
				embed.setDescription("Vous avez supprimé " + count + " messages !");
				channel.sendMessage(embed.build()).queue();
				
				history = new MessageHistory(channel);
				long id = history.retrievePast(1).complete().get(0).getIdLong();
				
				Timer timer = new Timer(5000, new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						channel.deleteMessageById(id).complete();
					}
				});
				timer.setRepeats(false);
				timer.start();
				DiscordLogger.logCommand(command, args);
				return CommandResult.SUCCESS;
			} else {
				command.getMessenger().commandInvalidArgs(command);
				return CommandResult.INVALID_ARGS;
			}
			
		} else {
			command.getMessenger().permissionDenied(command);
			return CommandResult.MISSING_PERMISSIONS;
		}
	}
	
	@Override
	public boolean canBypassCommandChannel() {
		return true;
	}

}
