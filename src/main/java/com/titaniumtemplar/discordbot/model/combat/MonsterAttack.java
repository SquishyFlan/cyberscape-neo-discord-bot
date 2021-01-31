package com.titaniumtemplar.discordbot.model.combat;

import com.titaniumtemplar.discordbot.model.character.CharStats;
import lombok.Builder;
import lombok.Data;

/*
	Class: MonsterAttack
	Description: Object detailing a specific monster attack
*/
@Data
@Builder
public class MonsterAttack {

	String monsterName;
	CharStats target;
	int damage;
	AttackType attackType;
	Specialization spec;

	/*
		Method: getDamageString
		Description: Returns string form of this monster attack
		output: String object with monster attack in output form
	*/
	public String getDamageString() {
		return monsterName + " " + attackType.getVerb()
			+ " " + target.getName() + " for " + damage + " damage! ["
			+ (target.getHpCurrent() - damage) + "HP]";
	}
}
