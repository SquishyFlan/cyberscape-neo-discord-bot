package com.titaniumtemplar.discordbot.model.combat;

import com.titaniumtemplar.discordbot.model.character.CharStats;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonsterAttack {

	String monsterName;
	CharStats target;
	int damage;
	AttackType attackType;
	Specialization spec;

	public String getDamageString() {
		return monsterName + " " + attackType.getVerb()
			+ " " + target.getName() + " for " + damage + " damage! ["
			+ (target.getHpCurrent() - damage) + "HP]";
	}
}
