package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.model.combat.AttackType.SHOOT;

import com.titaniumtemplar.discordbot.model.combat.AttackType;
import java.util.List;
import lombok.RequiredArgsConstructor;

/*
	Class: ShootCommand
	Description: Shoot Command Object
*/
@RequiredArgsConstructor(staticName = "withArgs")
public class ShootCommand extends CombatCommand {

  private final List<String> splitCommand;

	/*
		Method: AttackType
		Description: Returns attack Type
		Output: SHOOT
	*/
  @Override
  protected AttackType getAttackType() {
    return SHOOT;
  }

	/*
		Method: canSpecialize
		Description: Returns if object can be specialized
		Output: False
	*/
  @Override
  protected boolean canSpecialize() {
    return false;
  }

	/*
		Method: getCommand
		Description: returns this command
		Output: List object
	*/
  @Override
  protected List<String> getCommand() {
    return splitCommand;
  }
}
