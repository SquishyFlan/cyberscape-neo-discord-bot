package com.titaniumtemplar.discordbot.model.combat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attack {

	String charName;
	int damage;
	AttackType attackType;
	Specialization spec;

	public String getCombatantString() {
		return charName + " - "
			+ attackType.getDisplay()
			+ (spec != null ? " (" + spec.name() + ")" : "");
	}

	public String getDamageString() {
		return charName + " " + attackType.getVerb()
			+ " for " + damage + " damage!";
	}
}
