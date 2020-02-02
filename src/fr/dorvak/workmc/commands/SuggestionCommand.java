package fr.dorvak.workmc.commands;

import java.awt.Color;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.CommandSenderType;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class SuggestionCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) {
			command.getMainInstance().getLogger().sendConsoleError("Only a discord user can perform this command");
			return CommandResult.COMMAND_NOT_ALLOWED;
		}
		
		User user = command.getPerformer();
		JDA jda = command.getMainInstance().getJDA();
		
		if(args[0].equals("suggest") && args.length == 1) {
			WorkMc.getInstance().getMessenger().commandInvalidArgs(command);
			return CommandResult.INVALID_ARGS;
		}
		
		TextChannel channel = jda.getTextChannelById(Constants.SUGGESTIONS_CHANNEL_ID);
		
		StringBuilder sb = new StringBuilder();
		
		for(String s : args) {
			sb.append(s + " ");
		}
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.GREEN);
		embed.setTitle("Nouvelle suggestion !");
		embed.addField("", "**Nouvelle suggestion de " + user.getAsMention() + " :** \n\n > " + sb.toString().toUpperCase().replace(sb.toString().toUpperCase().substring(1), sb.toString().substring(1).toLowerCase()) , false);
		
		channel.sendMessage(embed.build()).queue();
		
		MessageHistory history = new MessageHistory(channel);
		Message msg = history.retrievePast(1).complete().get(0);
		msg.addReaction("✅").queue();
		msg.addReaction("❌").queue();
		
		EmbedBuilder result = EmbedHelper.getBasicEmbed();
		result.setColor(Color.GREEN);
		result.setTitle("Et voilà !");
		result.setDescription("> Votre suggestion a été envoyée avec succès dans le salon " + channel.getAsMention() + ".");
		command.getTextChannel().sendMessage(result.build()).queue();
		
		return CommandResult.SUCCESS;
	}
}

