package com.titaniumtemplar.discordbot.model.combat;

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
