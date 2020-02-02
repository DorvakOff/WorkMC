package fr.dorvak.workmc.utils;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.core.Permission;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class Constants {

	public static final long GUILD_ID = 580065637238439946L;
	public static final long COMMAND_CHANNEL_ID = 673295889078419481L;
	public static final long CATEGORY_TICKET_ID = 657576804080418816L;
	public static final Permission CLOSE_TICKET_PERMISSION = Permission.MESSAGE_MANAGE;
	public static final long BASIC_ROLE_ID = 580092573545136133L;
	public static final long PROJECT_CHANNEL_ID = 673286069969616925L;
	public static final long LOG_CHANNEL_ID = 673470248984379405L;
	public static final long TICKET_CHANNEL_ID = 673285993729622056L;
	public static final String BOT_TOKEN = "NjU0MzgxNTUxNzEwMTA5Njk2.XfPQhQ.5p888uQcta9U6hxAwRmqS5jiz4o";
	public static final long DISPONIBILITY_CHANNEL_ID = 673473341046259753L;
	public static final long SUGGESTIONS_CHANNEL_ID = 673286021861081112L;
	public static final long WELCOME_CHANNEL_ID = 580065637238439949L;
	
	public static final String DATABASE_ADDRESS = "164.132.125.4";
	public static final String DATABASE_PASSWORD = "th9o&5W8";
	
	public static final List<Long> ALLOWEDS_COMMAND_AVAILABLE = Arrays.asList(
			/* Dev java */ 673273567625871361L,
			/* Dev web */ 673273669635538988L,
			/* Community manager */ 673274108410069013L,
			/* SysAdmin */ 673274361066553384L,
			/* Builder */ 673277191911374853L,
			/* Redacteur */ 673274257249402923L,
			/* Gfx */ 673274686834212895L,
			/* Modelisateur */ 673277361352998954L, 
			/* Trailer maker */ 673274758607011851L,
			/* Game designer */ 673281291600396288L,
			/* Youtubeur */ 673282483042910208L);

}
