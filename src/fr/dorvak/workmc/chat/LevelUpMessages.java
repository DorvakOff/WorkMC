package fr.dorvak.workmc.chat;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelUpMessages {
	
	private static List<String> messages;
	
	public LevelUpMessages() {
		messages = new ArrayList<>();
		load();
	}

	private void load() {
		add("Mais c'était sur enfait, %X% est encore en train de se boost !");
		add("Qui peut l'égaler ? %X% est dans la place !");
		add("%X% passe au niveau %L%, c'est presque son QI, **INCROYABLE !**");
		add("Après tant d'efforts, %X% passe enfin au niveau %L% !");
		add("%X% passe seulement au niveau %L%, **C'EST HONTEUX !!!**");
		add(":scream: OMG :scream: %X% passe au niveau %L%, c'est autant de cos que sur Epikube !");
		add("Woah gg ! %X% passe au level %L%, il a enfin dépassé son montant d'argent sur Paypal !");
		add("%X% atteint le niveau %L%, c'est autant d'argent que nous doit Luzrod...");
		add("Le niveau %L%, c'est presque autant que le nombre de dramas par jours sur ce discord :scream: !");
		add("%X% passe au niveau %L%, ça se rapproche du ping de Dorvak :wesh:");
	}
	
	private void add(String s) {
		messages.add("> " + s);
	}

	public static String random() {
		return messages.get(new Random().nextInt(messages.size()));
	}
}
