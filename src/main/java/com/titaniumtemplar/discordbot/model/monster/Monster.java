package com.titaniumtemplar.discordbot.model.monster;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Monster {
  private String name;
  private int currentHp;
  private int maxHp;
  private int xp;

  public static Monster fromTemplate(MonsterTemplate template) {
    return Monster.builder()
      .name(template.getName())
      .currentHp(template.getMaxHp())
      .maxHp(template.getMaxHp())
      .xp(template.getXp())
      .build();
  }

  public boolean isDead() {
    return currentHp <= 0;
  }
}
