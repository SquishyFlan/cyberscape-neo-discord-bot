package com.titaniumtemplar.discordbot.model.combat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class Attack
{
  // TODO: Put this somewhere better
  @Getter
  @RequiredArgsConstructor
  public static enum AttackType {
    ATTACK("Attack", "attacks"),
    SHOOT("Shoot", "shoots"),
    BOLT("Bolt", "fires a manabolt");

    private final String display;
    private final String verb;
  }

  // TODO: This too
  public static enum Specialization {
    IMMOLATE,
    BURST,
    FREEZE,
    RAIN,
    ETC
  }

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
