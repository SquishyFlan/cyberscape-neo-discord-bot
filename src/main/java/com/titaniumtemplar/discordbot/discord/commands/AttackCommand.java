package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.model.combat.AttackType.ATTACK;

import com.titaniumtemplar.discordbot.model.combat.AttackType;
import java.util.List;
import lombok.RequiredArgsConstructor;

/*
	Class: AttackCommand
	Description: Returns commands involving Attack
*/
@RequiredArgsConstructor(staticName = "withArgs")
public class AttackCommand extends CombatCommand {

  private final List<String> splitCommand;

	/*
		Method: getAttackType
		Description: Returns that this command is an Attack
		Output: Attack object
	*/
  @Override
  protected AttackType getAttackType() {
    return ATTACK;
  }

	/*
		Method: canSpecialize
		Description: Returns that this command cannot be Specialized
		Output: False
	*/
  @Override
  protected boolean canSpecialize() {
    return false;
  }

	/*
		Method: getCommand
		Description: Returns this command
		Output: List with command
	*/
  @Override
  protected List<String> getCommand() {
    return splitCommand;
  }
}
