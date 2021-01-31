package com.titaniumtemplar.discordbot.model.combat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
	enum: AttackType
	Description: Lists various types of attacks and their combat action string
*/
@Getter
@RequiredArgsConstructor
public enum AttackType {
  ATTACK("Attack", "attacks"),
  SHOOT("Shoot", "shoots"),
  BOLT("Bolt", "fires a manabolt");

  private final String display;
  private final String verb;
}
