package com.titaniumtemplar.discordbot.model.combat;

/*
	Enum: Specialization
	Description: Contains the attack specializations
*/
public enum Specialization {
  IMMOLATE,
  BURST,
  FREEZE,
  RAIN,
  UNKNOWN;

  public static Specialization fromString(String spec) {
    try {
      return Specialization.valueOf(spec);
    } catch (IllegalArgumentException ex) {
      return UNKNOWN;
    }
  }
}
