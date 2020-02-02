package fr.dorvak.workmc.commands.levels;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

import java.awt.Color;
import java.util.HashMap;

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.CommandSenderType;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.chat.UserAndLevel;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

public class TopLevelsCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) return CommandResult.COMMAND_NOT_ALLOWED;

		TextChannel channel = command.getTextChannel();
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.CYAN);
		embed.setTitle("Voici le classement des membres par niveau");
		
		HashMap<Integer, UserAndLevel> top = WorkMc.getInstance().getLevelManager().getBestLevels();
		
		embed.setDescription("> :trophy: **>** " + WorkMc.getInstance().getJDA().getUserById(top.get(1).getId()).getAsMention() + " est au niveau " + top.get(1).getLevel() + " !");
		
		for(int i = 2; i < 11; i++) {
			try {
			embed.appendDescription("\n > #" + i + " **>** " + WorkMc.getInstance().getJDA().getUserById(top.get(i).getId()).getAsMention() + " est au niveau " + top.get(i).getLevel() + " !");
			} catch (Exception e) {}
		}
		
		channel.sendMessage(embed.build()).queue();
		
		return CommandResult.SUCCESS;
	}
}
