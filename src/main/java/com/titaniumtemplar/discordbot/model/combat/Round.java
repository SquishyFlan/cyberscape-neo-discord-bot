package com.titaniumtemplar.discordbot.model.combat;

import com.titaniumtemplar.discordbot.model.monster.Monster;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.joining;

@Getter
@RequiredArgsConstructor
public class Round {

	private final int number;
	private final Map<String, Attack> attacks = new LinkedHashMap<>();

	String resolve(Monster monster) {
		if (attacks.isEmpty()) {
			return monster.getName() + " starts to look restless...";
		}

		String roundString = attacks.values()
			.stream()
			.peek(monster::applyAttack)
			.map(Attack::getDamageString)
			.collect(joining("\n"));

		monster.handleShields();

		return roundString;
	}

	void addAttack(String participantId, Attack attack) {
		if (attacks.containsKey(participantId)) {
			return;
		}
		attacks.put(participantId, attack);
	}

	void removeAttack(String participantId) {
		attacks.remove(participantId);
	}

	Collection<String> getParticipants() {
		return attacks.keySet();
	}

	public boolean isEmpty() {
		return attacks.isEmpty();
	}
}
