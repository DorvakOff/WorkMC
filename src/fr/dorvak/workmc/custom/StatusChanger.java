package fr.dorvak.workmc.custom;

import java.util.TimerTask;

import fr.dorvak.workmc.WorkMc;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Member;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class StatusChanger extends TimerTask {
	
	private JDA jda;
	private int i;
	
	public StatusChanger() {
		jda = WorkMc.getInstance().getJDA();
		jda.getPresence().setGame(Game.of(GameType.LISTENING, WorkMc.getInstance().getPrefixOnDiscord() + "help - v" + WorkMc.getVersion()));
		i = 0;
	}
	
	@Override
	public void run() {	
		i++;
		
		switch (i) {
			
		case 1:
			int online = 0;
			for(Member a : jda.getGuildById(WorkMc.getInstance().getGuildIdLong()).getMembers()){
				if(a.getOnlineStatus().toString().equals("ONLINE") || a.getOnlineStatus().toString().equals("DO_NOT_DISTURB") || a.getOnlineStatus().toString().equals("IDLE")) online++;
			}
			jda.getPresence().setGame(Game.of(GameType.DEFAULT, online-1 + " Membres en ligne"));
			break;
			
		case 2:
			jda.getPresence().setGame(Game.of(GameType.DEFAULT, jda.getGuildById(WorkMc.getInstance().getGuildIdLong()).getMembers().size() + " Membres"));
			break;
		
		case 3:
			jda.getPresence().setGame(Game.of(GameType.LISTENING, WorkMc.getInstance().getPrefixOnDiscord() + "help - v" + WorkMc.getVersion()));
			break;
		}
		
		if (i == 3){
			i = 0;
		}
	}
}
