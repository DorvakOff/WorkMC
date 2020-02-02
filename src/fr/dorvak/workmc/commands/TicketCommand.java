package fr.dorvak.workmc.commands;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.CommandSenderType;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class TicketCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) {
			command.getMainInstance().getLogger().sendConsoleError("Only a discord user can perform this command");
			return CommandResult.COMMAND_NOT_ALLOWED;
		}
		
		User user = command.getPerformer();
		JDA jda = command.getMainInstance().getJDA();
		Member member = jda.getGuildById(Constants.GUILD_ID).getMember(user);
		TextChannel current = command.getTextChannel();
		
		if(args.length >= 1 && args[0].equalsIgnoreCase("create")) {
			if(!(current.getIdLong() == Constants.TICKET_CHANNEL_ID)) {
				command.getMessenger().commandInvalidChannel(command);
				return CommandResult.COMMAND_NOT_ALLOWED;
			}
			StringBuilder sb = new StringBuilder();
			
			for(String s : args) {
				sb.append(s + " ");
			}
			
			String title = sb.toString().split("create ")[1];
			
			boolean creating = true;
			int i = 1;
			TextChannel ticket = null;
			
			while(creating) {
				
				try {
					ticket = jda.getGuildById(Constants.GUILD_ID).getTextChannelsByName("ticket-" + i, true).get(0);
					i++;
				} catch (Exception ignored){
					ticket = (TextChannel) jda.getCategoryById(Constants.CATEGORY_TICKET_ID).createTextChannel("ticket-" + i).complete();
					ticket.createPermissionOverride(jda.getGuildById(Constants.GUILD_ID).getMember(jda.getSelfUser())).setAllow(Permission.MESSAGE_READ).complete();
					ticket.createPermissionOverride(member).setAllow(Permission.MESSAGE_READ).complete();
					creating = false;
				}
			}
			
			current.sendMessage(user.getAsMention() + " votre ticket a été créé dans le salon " + ticket.getAsMention()).queue();
			
			EmbedBuilder embed = EmbedHelper.getBasicEmbed();
			embed.setColor(Color.GRAY);
			embed.setTitle("Nouveau ticket de " + user.getName());
			embed.setDescription("Sujet du ticket : **" + title + "**");
			ticket.sendMessage(embed.build()).queue();
		} else if(args[0].equalsIgnoreCase("close")) {
			if(member.hasPermission(Constants.CLOSE_TICKET_PERMISSION)) {
				
				if(current.getName().startsWith("ticket")) {
					current.sendMessage("Fermeture du ticket dans 10 secondes " + user.getAsMention()).queue();
					Timer timer = new Timer(10000, new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							current.delete().complete();
						}
					});
					timer.setRepeats(false);
					timer.start();
				}
			} else {
				command.getMessenger().permissionDenied(command);
			}
		} else {
			if(current.getIdLong() != Constants.TICKET_CHANNEL_ID) {
				TextChannel channel = command.getTextChannel();
				Message message = command.getMessage();
				
				channel.deleteMessageById(message.getId()).complete();
				
				EmbedBuilder embed = EmbedHelper.getBasicEmbed();
				embed.setColor(Color.RED);
				embed.setTitle("Oups, mauvais salon !");
				embed.setDescription("> Vous devez utiliser cette commande dans le salon " + jda.getTextChannelById(Constants.TICKET_CHANNEL_ID).getAsMention());
				channel.sendMessage(embed.build()).queue();
				
				MessageHistory history = new MessageHistory(channel);
				long messageId = history.retrievePast(1).complete().get(0).getIdLong();
				Timer timer = new Timer(5000, new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						channel.deleteMessageById(messageId).complete();
					}
				});
				timer.setRepeats(false);
				timer.start();
				return CommandResult.COMMAND_NOT_ALLOWED;
			}
			EmbedBuilder embed = EmbedHelper.getBasicEmbed();
			embed.setColor(Color.RED);
			embed.setTitle("Oups, mauvais arguments...");
			embed.setDescription("Veuillez faire " + WorkMc.getInstance().getPrefixOnDiscord() + "ticket create <Sujet>");
			command.getTextChannel().sendMessage(embed.build()).queue();
		}
		return null;
	}
	
	@Override
	public boolean canBypassCommandChannel() {
		return true;
	}
}
