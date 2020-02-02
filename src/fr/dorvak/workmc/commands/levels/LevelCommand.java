package fr.dorvak.workmc.commands.levels;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

import fr.dorvak.betterjda.lite.commands.Command;
import fr.dorvak.betterjda.lite.commands.CommandResult;
import fr.dorvak.betterjda.lite.commands.CommandSenderType;
import fr.dorvak.betterjda.lite.commands.listeners.CommandListener;
import fr.dorvak.workmc.utils.images.LevelImage;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class LevelCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {
		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) return CommandResult.COMMAND_NOT_ALLOWED;

		TextChannel channel = command.getTextChannel();
		Message message = command.getMessage();
		
		LevelImage img = null;
		
		if(args.length == 0 || message.getMentionedUsers().size() == 0) {
			img = new LevelImage(command.getPerformer());
		} else {
			img = new LevelImage(message.getMentionedUsers().get(0));
		}
		
		channel.sendFile(img.get()).queue();
		
		return CommandResult.SUCCESS;
	}

}
