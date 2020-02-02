package fr.dorvak.workmc.commands;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class SearchCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		
		StringBuilder sb = new StringBuilder();
		
		for(String s : args) {
			sb.append(s + "+");
		}
		
		embed.setTitle("En espérant que ça puisse t'aider !");
		embed.setDescription("https://lmgtfy.com/?q=" + sb.toString());
		
		command.getTextChannel().sendMessage(embed.build()).queue();
		
		return null;
	}
	
	@Override
	public boolean canBypassCommandChannel() {
		return true;
	}

}
