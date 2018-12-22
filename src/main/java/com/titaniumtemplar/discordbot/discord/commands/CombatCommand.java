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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public abstract class CombatCommand implements DiscordCommand {

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

	private Specialization getSpecialization() {
		List<String> splitCommand = getCommand();
		if (splitCommand.size() < 2) {
			return null;
		}
		return Specialization.fromString(splitCommand.get(1));
	}

	protected abstract List<String> getCommand();

	protected abstract AttackType getAttackType();

	protected abstract boolean canSpecialize();
}
