package fr.dorvak.workmc.listeners;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.chat.LevelsManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class MessageListener extends ListenerAdapter {
	
	private Map<String, Long> cooldowns;
	private int delay;
	
	public MessageListener() {
		cooldowns = new HashMap<>();
		delay = 10;
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
			LevelsManager lManager = WorkMc.getInstance().getLevelManager();
			User user = event.getMember().getUser();
			Message message = event.getMessage();
			TextChannel channel = event.getChannel();
			
			if(user.isBot() || message.getContentDisplay().replace(" ", "").replace(".", "").length() <= 5 || message.getContentRaw().startsWith(":") || message.getContentRaw().startsWith(WorkMc.getInstance().getPrefixOnDiscord())) {
				return;
			}
			
			if(cooldowns.containsKey(user.getId())) {
				long left = cooldowns.get(user.getId()) / 1000 + delay - Instant.now().toEpochMilli() / 1000;
				
				if(left <= 0) {
					cooldowns.put(user.getId(), Instant.now().toEpochMilli());
					if(!lManager.hasAccount(user.getId())) {
						lManager.createAccount(user.getId());
					} else {
						lManager.addExp(user.getId(), channel);
					}
				}
			} else {
				cooldowns.put(user.getId(), Instant.now().toEpochMilli());
				if(!lManager.hasAccount(user.getId())) {
					lManager.createAccount(user.getId());
				} else {
					lManager.addExp(user.getId(), channel);
				}
			}
		super.onGuildMessageReceived(event);
	}
}
