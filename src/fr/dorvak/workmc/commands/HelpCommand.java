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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.UserImpl;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class HelpCommand extends CommandListener {

	@Override
	public CommandResult perform(Command command, String[] args) {

		if(command.getCommandSenderType() == CommandSenderType.CONSOLE) return CommandResult.COMMAND_NOT_ALLOWED;
		
		User user = command.getPerformer();
		String prefix = WorkMc.getInstance().getPrefixOnDiscord();
		TextChannel channel = command.getTextChannel();
		Member member = WorkMc.getInstance().getJDA().getGuildById(Constants.GUILD_ID).getMember(user);
		
		EmbedBuilder embed = EmbedHelper.getBasicEmbed();
		embed.setColor(Color.CYAN);
		embed.setTitle("Voici la liste des commandes : (préfixe: !)");
		embed.addField(prefix + "help", "Vous affiche la commande d'aide.", false);
		embed.addField(prefix + "ticket", "Créez un ticket.", false);
		embed.addField(prefix + "level / " + prefix + "rank <@membre (optionnel)>", "Obtenez votre level ou celui d'un membre.", false);
		embed.addField(prefix + "levels / " + prefix + "top", "Regardez le classement des niveaux du serveur.", false);
		embed.addField(prefix + "google", "T'aide à chercher quand tu y arrive pas.", false);
		embed.addField(prefix + "dispo list", "Affiche la liste des vendeurs de services disponible.", false);
		embed.addField(prefix + "suggest", "Permet de faire une suggestion.", false);
		
		EmbedBuilder more = EmbedHelper.getBasicEmbed();
		more.setColor(Color.CYAN);
		more.setTitle("Commandes supplémentaires : ");
		
		if(AvailableCommand.canUseCommand(member)) {
			more.addField(prefix + "dispo", "Affichez vous comme disponible ou indisponible.", false);
		}
		
		if(member.hasPermission(Permission.KICK_MEMBERS)) {
			more.addField(prefix + "kick <joueur> <raison (optionnel)>", "Excluez un joueur.", false);
		}
		
		if(member.hasPermission(Permission.BAN_MEMBERS)) {
			more.addField(prefix + "ban <joueur> <raison (optionnel)>", "Bannissez un joueur.", false);
			more.addField(prefix + "tempban <joueur> <temps (en jours)> <raison (optionnel)>", "Bannissez un joueur temporairement.", false);
			more.addField(prefix + "unban <joueur>", "Débannissez un joueur.", false);
		}
		
		if(member.hasPermission(Permission.MESSAGE_MANAGE)) {
			more.addField(prefix + "warn <joueur> <raison (optionnel)>", "Avertissez un joueur.", false);
			more.addField(prefix + "warns <joueur>", "Récupérez la liste des avertissements d'un joueur.", false);
			more.addField(prefix + "mute <joueur> <raison>", "Rendez muet un joueur.", false);
			more.addField(prefix + "tempmute <joueur> <temps> <raison>", "Rendez muet un joueur temporairement.", false);
			more.addField(prefix + "unmute <joueur>", "Libérez ce joueur du silence.", false);
			more.addField(prefix + "clear <montant>", "Nettoyez les messages d'un salon", false);
		}
		
		
		if (!user.hasPrivateChannel()) user.openPrivateChannel().complete();
		
		((UserImpl)user).getPrivateChannel().sendMessage(embed.build()).submit()
			.whenComplete((v, error) -> {
				if(error != null) {
					channel.sendMessage("Vos messages privés semblent bloqués, envoie de la liste des commandes ici...").submit();
					channel.sendMessage(embed.build()).submit();
					if(more.getFields().size() != 0) {
						channel.sendMessage(more.build()).queue();
					}
				} else {
					if(more.getFields().size() != 0) {
						((UserImpl) user).getPrivateChannel().sendMessage(more.build()).queue();
					}
					channel.sendMessage("La liste des commandes vous a été envoyé en message privé " + user.getAsMention()).submit();
				}
			});
		
		return CommandResult.SUCCESS;
	}
}
