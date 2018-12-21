package com.titaniumtemplar.discordbot.model.combat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttackType {
  ATTACK("Attack", "attacks"),
  SHOOT("Shoot", "shoots"),
  BOLT("Bolt", "fires a manabolt");

  private final String display;
  private final String verb;
}
