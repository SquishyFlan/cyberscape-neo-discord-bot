package com.titaniumtemplar.discordbot.model.monster;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonsterTemplate {
  private UUID id;

  private String name;
  private int maxHp;
  private int xp;
}
