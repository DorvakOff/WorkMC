package fr.dorvak.workmc;

import java.util.concurrent.TimeUnit;

import fr.dorvak.betterjda.lite.bot.DiscordBot;
import fr.dorvak.betterjda.lite.commands.CommandFactory;
import fr.dorvak.betterjda.lite.utils.EmbedHelper;
import fr.dorvak.workmc.chat.LevelUpMessages;
import fr.dorvak.workmc.chat.LevelsManager;
import fr.dorvak.workmc.commands.AvailableCommand;
import fr.dorvak.workmc.commands.HelpCommand;
import fr.dorvak.workmc.commands.SearchCommand;
import fr.dorvak.workmc.commands.SuggestionCommand;
import fr.dorvak.workmc.commands.TicketCommand;
import fr.dorvak.workmc.commands.levels.LevelCommand;
import fr.dorvak.workmc.commands.levels.TopLevelsCommand;
import fr.dorvak.workmc.commands.mod.BanCommand;
import fr.dorvak.workmc.commands.mod.ClearCommand;
import fr.dorvak.workmc.commands.mod.KickCommand;
import fr.dorvak.workmc.commands.mod.MuteCommand;
import fr.dorvak.workmc.commands.mod.TempBanCommand;
import fr.dorvak.workmc.commands.mod.TempMuteCommand;
import fr.dorvak.workmc.commands.mod.UnbanCommand;
import fr.dorvak.workmc.commands.mod.UnmuteCommand;
import fr.dorvak.workmc.commands.mod.WarnCommand;
import fr.dorvak.workmc.commands.mod.WarnListCommand;
import fr.dorvak.workmc.custom.CustomMessenger;
import fr.dorvak.workmc.custom.DisponibilityChecker;
import fr.dorvak.workmc.custom.StatusChanger;
import fr.dorvak.workmc.listeners.GuildMemberListener;
import fr.dorvak.workmc.listeners.MessageListener;
import fr.dorvak.workmc.mod.BanManager;
import fr.dorvak.workmc.mod.MuteManager;
import fr.dorvak.workmc.mod.WarnManager;
import fr.dorvak.workmc.utils.Constants;
import fr.dorvak.workmc.utils.DiscordLogger;
import fr.dorvak.workmc.utils.MultiThreading;
import fr.dorvak.workmc.utils.database.TableManager;
import fr.dorvak.workmc.utils.database.core.DatabaseManager;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class WorkMc extends DiscordBot {
	
	private static String version = "2.0.0";
	private static WorkMc INSTANCE;
	private DatabaseManager databaseManager;
	private TableManager tableManager;
	private LevelsManager levelsManager;
	private WarnManager warnManager;
	private BanManager banManager;
	private MuteManager muteManager;

	protected WorkMc() throws Exception {
		super();
	}
	
	public static void main(String[] args) {
		try {
			new WorkMc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onLoad() {
		getLogger().sendConsoleSuccess("WorkMC loading... v" + version);
		setToken(Constants.BOT_TOKEN);
		setGuildId(Constants.GUILD_ID);
		CommandFactory.setPrefixOnDiscord("!");
		CommandFactory.setCommandChannel(Constants.COMMAND_CHANNEL_ID);
		INSTANCE = this;
		super.onLoad();
	}
	
	@Override
	public void onStart() {
		/*
		 * Custom
		 */
		EmbedHelper.setFooterText("WorkMc - v" + version);
		
		setMessenger(new CustomMessenger());
		
		MultiThreading.schedule(new StatusChanger(), 20, TimeUnit.SECONDS);
		MultiThreading.schedule(new DisponibilityChecker(this), 5, TimeUnit.MINUTES);
		
		new LevelUpMessages();
		new DiscordLogger(this);
		
		/*
		 * Database
		 */
		
		databaseManager = new DatabaseManager(this);
		tableManager = new TableManager(this);
		levelsManager = new LevelsManager(this);
		
		/*
		 * Listeners
		 */
		
		getJDA().addEventListener(new GuildMemberListener());
		getJDA().addEventListener(new MessageListener());
		
		/*
		 * Simple Commands
		 */
		
		registerCommand("help", new HelpCommand());
		registerCommand("google", new SearchCommand());

		/*
		 * Utils Commands
		 */
		
		registerCommand("ticket", new TicketCommand());
		registerCommand("dispo", new AvailableCommand());
		registerCommand("suggest", new SuggestionCommand());
		
		/*
		 * Level Commands
		 */
		
		registerCommand("levels", new TopLevelsCommand());
		registerCommand("top", new TopLevelsCommand());
		
		registerCommand("level", new LevelCommand());
		registerCommand("rank", new LevelCommand());
		
		/*
		 * Mod commands
		 */
		
		warnManager = new WarnManager(this);
		banManager = new BanManager(this);
		muteManager = new MuteManager(this);
		
		registerCommand("kick", new KickCommand());
		registerCommand("ban", new BanCommand());
		registerCommand("tempban", new TempBanCommand());
		registerCommand("unban", new UnbanCommand());
		registerCommand("warn", new WarnCommand());
		registerCommand("warns", new WarnListCommand());
		registerCommand("mute", new MuteCommand());
		registerCommand("unmute", new UnmuteCommand());
		registerCommand("tempmute", new TempMuteCommand());
		registerCommand("clear", new ClearCommand());
		
		banManager.run();
		muteManager.run();
		super.onStart();
	}
	
	@Override
	public void onStop() {
		MultiThreading.POOL.shutdown();
		MultiThreading.RUNNABLE_POOL.shutdown();
		super.onStop();
	}
	
	public static WorkMc getInstance() {
		return INSTANCE;
	}
	
	public static String getVersion() {
		return version;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	public TableManager getTableManager() {
		return tableManager;
	}

	public LevelsManager getLevelManager() {
		return levelsManager;
	}

	public MuteManager getMuteManager() {
		return muteManager;
	}

	public BanManager getBanManager() {
		return banManager;
	}

	public WarnManager getWarnManager() {
		return warnManager;
	}
}
