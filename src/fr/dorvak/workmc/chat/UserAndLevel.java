package fr.dorvak.workmc.chat;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class UserAndLevel {
	
	private String id;
	private int level;
	
	public UserAndLevel(String id, int level) {
		this.id = id;
		this.level = level;
	}
	
	public String getId() {
		return id;
	}
	
	public int getLevel() {
		return level;
	}

}
