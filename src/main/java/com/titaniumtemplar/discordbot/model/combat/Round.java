package com.titaniumtemplar.discordbot.model.combat;

import com.titaniumtemplar.discordbot.model.monster.Monster;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.joining;

import com.titaniumtemplar.discordbot.model.character.CharStats;
import java.util.HashMap;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class Round {

	private final int number;
	private final Map<String, Attack> attacks = new LinkedHashMap<>();
	private final Map<String, CharStats> participants = new HashMap<>();

	String resolve(Monster monster, MonsterAttack attack) {

		Stream<String> combatLines = attacks.values()
			.stream()
			.peek(monster::applyAttack)
			.map(Attack::getDamageString);

		if (!monster.isDead() && attack.getTarget() != null) {
			combatLines = Stream.concat(combatLines, Stream.of(attack.getDamageString()));
			attack.getTarget().applyAttack(attack);
		}

		if (attacks.isEmpty()) {
			combatLines = Stream.concat(
				combatLines,
				Stream.of(monster.getName() + " starts to look restless..."));
		}

		String roundString = combatLines
			.collect(joining("\n"));

		monster.handleShields();

		return roundString;
	}

	void addAttack(CharStats character, Attack attack) {
		var participantId = character.getUserId();
		if (attacks.containsKey(participantId)) {
			return;
		}
		attacks.put(participantId, attack);
		participants.put(participantId, character);
	}

	void removeAttack(String participantId) {
		attacks.remove(participantId);
		participants.remove(participantId);
	}

	public boolean isEmpty() {
		return attacks.isEmpty();
	}
}
