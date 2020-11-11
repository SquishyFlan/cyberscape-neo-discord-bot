package com.titaniumtemplar.discordbot.model.combat;

import static com.titaniumtemplar.db.jooq.enums.StatType.dex;
import static com.titaniumtemplar.db.jooq.enums.StatType.int_;
import static com.titaniumtemplar.db.jooq.enums.StatType.str;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.ATTACK;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.BOLT;
import static com.titaniumtemplar.discordbot.model.combat.AttackType.SHOOT;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;

import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.monster.Monster;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

@Data
@NoArgsConstructor
public class Combat {

	private static final Random RAND = new Random();

	private Guild guild;
	private Monster monster;
	private Message message;
	private Round previousRound;
	private Round currentRound = new Round(1);
	private String lastRoundText = "";
	private int ignoredRounds = 0;

	private final Map<String, CharStats> participants = new HashMap<>();

	public void resolveRound() {
		previousRound = currentRound;
    participants.putAll(previousRound.getParticipants());
		lastRoundText = previousRound.resolve(monster, createMonsterAttack(monster));
		currentRound = new Round(previousRound.getNumber() + 1);
		if (previousRound.isEmpty()) {
			ignoredRounds++;
		} else {
			ignoredRounds = 0;
		}
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

		currentRound.addAttack(character, attack);
	}

	public void removeAttack(String userId) {
		currentRound.removeAttack(userId);
	}

	private void calculateDamage(CharStats character, Attack attack) {
		float baseDamage;
		switch (attack.getAttackType()) {
			case ATTACK:
			default:
				baseDamage = character.getStats().get(str).intValue();
				break;
			case SHOOT:
				baseDamage = character.getStats().get(dex).intValue();
				break;
			case BOLT:
				baseDamage = character.getStats().get(int_).intValue();
				break;
		}

		float randMultiplier = 0.9f + RAND.nextFloat() * 0.2f;
		baseDamage *= randMultiplier;

		if (monster.hasShield(attack.getAttackType())) {
			baseDamage *= 0.5f;
		}

		attack.setDamage(Math.round(baseDamage));
	}

	private MonsterAttack createMonsterAttack(Monster monster) {
		var topStat = monster.getStats()
			.entrySet()
			.stream()
			.filter((stat) -> stat.getKey() == str || stat.getKey() == dex || stat.getKey() == int_)
			.sorted(comparing((entry) -> entry.getValue().get(), reverseOrder()))
			.findFirst()
			.get();

		var baseDamage = topStat.getValue()
			.get();

		float randMultiplier = 0.6f + RAND.nextFloat() * 0.2f;
		baseDamage *= randMultiplier;

		AttackType attackType;
		switch (topStat.getKey())
		{
			case str:
				attackType = ATTACK;
				break;
			case dex:
				attackType = SHOOT;
				break;
			case int_:
				attackType = BOLT;
				break;
			default:
				attackType = ATTACK;
				break;
		}

		var validTargets = participants.values()
			.stream()
			.filter((charStats) -> charStats.getHpCurrent() > 0)
			.toArray(CharStats[]::new);

		CharStats target = null;
		if (validTargets.length > 0) {
			target = validTargets[RAND.nextInt(validTargets.length)];
		}

		return MonsterAttack.builder()
			.attackType(attackType)
			.damage(Math.round(baseDamage))
			.monsterName(monster.getName())
			.target(target)
			.build();
	}
}
