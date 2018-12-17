package com.titaniumtemplar.discordbot.model.monster;

import java.util.UUID;
import lombok.Data;

@Data
public class MonsterTemplate {
  private UUID id;
  private int spawnWeight;

  private String name;
  private int maxHp;
  private int xp;
}
