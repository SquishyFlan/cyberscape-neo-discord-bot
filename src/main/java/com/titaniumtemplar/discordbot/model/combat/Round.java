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

/*
	Class: Round
	Description: Object of current round in combat
*/
@Getter
@RequiredArgsConstructor
public class Round {

	private final int number;
	private final Map<String, Attack> attacks = new LinkedHashMap<>();
	private final Map<String, CharStats> participants = new HashMap<>();

	/*
		Method: resolve
		Description: Handles monster and monster attack
		Input: Monster object, MonsterAttack object
		Output: String that has the text output for the entire round
	*/
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

	/*
		Method: addAttack
		Description: Adds another attack for specified character
		Input: CharStats object, Attack object
	*/
	void addAttack(CharStats character, Attack attack) {
		var participantId = character.getUserId();
		if (attacks.containsKey(participantId)) {
			return;
		}
		attacks.put(participantId, attack);
		participants.put(participantId, character);
	}

	/*
		Method: removeAttack
		Description: Removes attack from specified user
		Input: String with user's id
	*/
	void removeAttack(String participantId) {
		attacks.remove(participantId);
		participants.remove(participantId);
	}

	/*
		Method: isEmpty
		Description: Determines if there are any attacks in the queue
		Input: boolean
	*/
	public boolean isEmpty() {
		return attacks.isEmpty();
	}
}
