package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.discord.DiscordUtils.deleteMessage;
import static com.titaniumtemplar.discordbot.discord.DiscordUtils.sendDm;
import static com.titaniumtemplar.discordbot.model.combat.Specialization.UNKNOWN;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.combat.AttackType;
import com.titaniumtemplar.discordbot.model.combat.Combat;
import com.titaniumtemplar.discordbot.model.combat.Specialization;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/*
	Class: CombatCommand
	Description: Command class that handles combat commands
*/
public abstract class CombatCommand implements DiscordCommand {

	/*
		Method: run
		Description: Handles Commands relating to combat
		Input: CyberscapeService object, Myra object, Message object, User object, Member object
	*/
	@Override
	public void run(
		CyberscapeService service,
		Myra myra,
		Message message,
		User author,
		Member member) {
		try {
			if (member == null) {
				member = myra.getMember(author);
				if (member == null) {
					sendDm(author, "I'm sorry, you don't seem to be connected to any Cyberscape Neo compatible server!");
					return;
				}
			}
			Combat combat = myra.getCombat(member);
			if (combat == null) {
				sendDm(author, "I'm sorry, I couldn't find any combat for you to join!");
				return;
			}
			CharStats character;
			try {
				character = service.getCharacter(author.getId());
			} catch (NoSuchCharacterException ex) {
				sendDm(author, "I'm sorry, I couldn't find your character. Please use \".register\" to join!");
				return;
			}
			character.setName(member.getEffectiveName());

			if (character.isDead()) {
				sendDm(author, "I'm sorry, but your character is dead and cannot continue this combat!");
				return;
			}

			Specialization specialization = getSpecialization();

			if (specialization != null) {
				if (specialization == UNKNOWN) {
					sendDm(author, "I'm sorry, I didn't recognize that specialization!");
					return;
				} else if (!canSpecialize()) {
					sendDm(author, "I'm sorry, but that attack type cannot specialize!");
					return;
				} else if (!character.getSpecs().contains(specialization)) {
					sendDm(author, "I'm sorry, but your character doesn't know that specialization!");
					return;
				}
			}

			synchronized (combat) {
				combat.addAttack(character, getAttackType(), specialization);
			}
			myra.updateCombatMessage(combat);
		} finally {
			deleteMessage(message);
		}
	}

	/*
		Method: getSpecialization
		Description: Retrieves this funciton's specialization
		Output: Specialization object
	*/
	private Specialization getSpecialization() {
		List<String> splitCommand = getCommand();
		if (splitCommand.size() < 2) {
			return null;
		}
		return Specialization.fromString(splitCommand.get(1));
	}

	/*
		Method: getCommand
		Description: Abstract function that returns current command
	*/
	protected abstract List<String> getCommand();

	/*
		Method: getAttackType
		Description: Abstract function that returns current attack type
	*/
	protected abstract AttackType getAttackType();

	/*
		Method: getCommand
		Description: Abstract function that returns if this can be specialized
	*/
	protected abstract boolean canSpecialize();
}
