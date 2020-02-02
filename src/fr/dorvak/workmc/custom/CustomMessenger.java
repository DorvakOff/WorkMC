package fr.dorvak.workmc.custom;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import fr.dorvak.betterjda.lite.bot.DiscordBot;
import fr.dorvak.betterjda.lite.commands.BasicMessenger;
import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandFactory;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class CustomMessenger extends BasicMessenger {

	@Override
	public void commandInvalidChannel(Command command) {
		TextChannel channel = command.getTextChannel();
		Message message = command.getMessage();
		
		channel.deleteMessageById(message.getId()).complete();
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.RED);
		embed.setTitle("Oups, mauvais salon !");
		embed.setDescription("> Vous devez utiliser cette commande dans le salon " + DiscordBot.getInstance().getJDA().getGuildById(DiscordBot.getInstance().getGuildIdLong()).getTextChannelById(CommandFactory.getCommandChannelIdLong()).getAsMention());
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
	}

	@Override
	public void commandNotFound(Command command) {
		Message message = command.getMessage();
		TextChannel channel = command.getTextChannel();
		
		channel.deleteMessageById(message.getId()).complete();
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.RED);
		embed.setTitle("Commande inconnue !");
		embed.setDescription("> La commande '" + command.getName() + "' n'existe pas !");
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
	}

	@Override
	public void commandOnlyConsoleCan(Command command) {
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.RED);
		embed.setTitle("Vous n'êtes pas autorisé à faire cela !");
		embed.setDescription("> La commande '" + command.getName() + "' marche seulement en console !");
		
		TextChannel channel = command.getTextChannel();
		
		channel.sendMessage(embed.build()).queue();
	}

	@Override
	public void permissionDenied(Command command) {
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.RED);
		embed.setTitle("Vous ne pouvez pas faire cela !");
		embed.setDescription("> Il semblerait que vous n'ayez pas la permission requise !");
		
		TextChannel channel = command.getTextChannel();
		
		channel.sendMessage(embed.build()).queue();
	}

	@Override
	public void commandInvalidArgs(Command command) {
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.RED);
		embed.setTitle("Mauvaise utilisation de la commande !");
		embed.setDescription("> Il semble manquer des arguments pour cette commande !");
		
		TextChannel channel = command.getTextChannel();
		
		channel.sendMessage(embed.build()).queue();
	}

}
