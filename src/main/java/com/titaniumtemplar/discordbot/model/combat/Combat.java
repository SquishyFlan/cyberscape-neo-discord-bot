package com.titaniumtemplar.discordbot.model.combat;

import static com.titaniumtemplar.db.jooq.enums.StatType.dex;
import static com.titaniumtemplar.db.jooq.enums.StatType.int_;
import static com.titaniumtemplar.db.jooq.enums.StatType.str;

import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.monster.Monster;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

@Data
@NoArgsConstructor
public class Combat
{
  private static final Random RAND = new Random();

  private Guild guild;
  private Monster monster;
  private Message message;
  private Round previousRound;
  private Round currentRound = new Round(1);
  private String lastRoundText = "";

  private final Set<String> participantUids = new HashSet<>();

  public void resolveRound() {
    previousRound = currentRound;
    lastRoundText = previousRound.resolve(monster);
    currentRound = new Round(previousRound.getNumber() + 1);
  }

  public void addAttack(
      CharStats character,
      AttackType type,
      Specialization spec) {
    Attack attack = Attack.builder()
	.charName(character.getName())
	.attackType(type)
	.spec(spec)
	.build();

    calculateDamage(character, attack);

    currentRound.addAttack(character.getUserId(), attack);

    participantUids.add(character.getUserId());
  }

  private void calculateDamage(CharStats character, Attack attack) {
    float baseDamage;
    switch (attack.getAttackType()) {
      case ATTACK:
      default:
	baseDamage = 2.5f * character.getStats().get(str).intValue();
	break;
      case SHOOT:
	baseDamage = 2.5f * character.getStats().get(dex).intValue();
	break;
      case BOLT:
	baseDamage = 2.5f * character.getStats().get(int_).intValue();
	break;
    }

    float randMultiplier = 0.9f + RAND.nextFloat() * 0.2f;
    baseDamage *= randMultiplier;

    if (monster.hasShield(attack.getAttackType())) {
      baseDamage *= 0.5f;
    }

    attack.setDamage(Math.round(baseDamage));
  }
}
