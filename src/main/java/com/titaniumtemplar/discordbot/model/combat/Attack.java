package com.titaniumtemplar.discordbot.model.combat;

public class Attack
{
  // TODO: Put this somewhere better
  private static enum AttackType {
    ATTACK,
    SHOOT,
    BOLT
  }

  // TODO: This too
  private static enum Specialization {
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
}
