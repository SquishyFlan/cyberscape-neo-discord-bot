package com.titaniumtemplar.discordbot.model.combat;

import lombok.Builder;
import lombok.Data;

/*
	Class: Attack
	Description: Attack object
*/
@Data
@Builder
public class Attack {

	String charName;
	int damage;
	AttackType attackType;
	Specialization spec;

	/*
		Method: getCombatantString
		Description: Returns string on combatant
		Output: String with combat name (For text output)
	*/
	public String getCombatantString() {
		return charName + " - "
			+ attackType.getDisplay()
			+ (spec != null ? " (" + spec.name() + ")" : "");
	}

	/*
		Method: getDamageString
		Description: Returns string with how much damage is done
		Output: String with damage done (For text output)
	*/
	public String getDamageString() {
		return charName + " " + attackType.getVerb()
			+ " for " + damage + " damage!";
	}
}
