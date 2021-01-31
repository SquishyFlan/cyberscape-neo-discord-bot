package com.titaniumtemplar.discordbot.model.monster;

import com.titaniumtemplar.db.jooq.enums.SkillType;
import com.titaniumtemplar.db.jooq.enums.StatType;
import com.titaniumtemplar.discordbot.model.character.Skill;
import com.titaniumtemplar.discordbot.model.combat.Attack;
import com.titaniumtemplar.discordbot.model.combat.AttackType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.Data;

/*
	Class: Monster
	Description: Object for monster
*/
@Data
@Builder
public class Monster {

	private String name;
	private int currentHp;
	private int maxHp;
	private int xp;
	private Map<SkillType, Skill> skills;
	private Map<StatType, AtomicInteger> stats;

	private final Map<AttackType, AtomicInteger> defenseCharge = new HashMap<>();
	private final Map<AttackType, Boolean> shields = new HashMap<>();

	/*
		Method: fromTemplate
		Description: Creates a monster based on template
		Input: MonsterTemplate object
		Output: Monster object
	*/
	public static Monster fromTemplate(MonsterTemplate template) {
		return Monster.builder()
			.name(template.getName())
			.maxHp(template.getHpMax())
			.currentHp(template.getHpMax())
			.xp(template.getXp())
			.skills(template.getSkills())
			.stats(template.getStats())
			.build();
	}

	/*
		Method: isDead
		Description: Determines if monster is dead
		Output: boolean
	*/
	public boolean isDead() {
		return currentHp <= 0;
	}

	/*
		Method: applyAttack
		Description: Applies attack to monster
		Input: Attack object
	*/
	public void applyAttack(Attack attack) {
		currentHp -= Math.max(attack.getDamage(), 0);
		defenseCharge.computeIfAbsent(attack.getAttackType(), (__) -> new AtomicInteger())
			.addAndGet(20);
	}

	/*
		Method: handleShields
		Description: Evaluate how shields affect things
		Input: User Object, String Message
	*/
	public void handleShields() {
		defenseCharge.forEach((type, charge) -> {
			if (charge.get() >= 100) {
				shields.put(type, true);
				charge.addAndGet(-100);
			} else {
				shields.put(type, false);
			}
		});
	}

	/*
		Method: getDefenseCharge
		Description: Calculate Defense Charge level based on attack type received
		Input: AttackType object
		Output: return new Defense Charge level
	*/
	public int getDefenseCharge(AttackType attackType) {
		return defenseCharge.computeIfAbsent(attackType, (__) -> new AtomicInteger())
			.get();
	}

	/*
		Method: hasShield
		Description: Determines if monster has shields against current attack type
		Input: AttackType object
		Output: boolean
	*/
	public boolean hasShield(AttackType attackType) {
		return shields.getOrDefault(attackType, false);
	}
}
