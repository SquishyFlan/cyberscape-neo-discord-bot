package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.model.combat.AttackType.BOLT;

import com.titaniumtemplar.discordbot.model.combat.AttackType;
import java.util.List;
import lombok.RequiredArgsConstructor;

/*
	Class: BoltCommand
	Description: Class that govern the Botl command
*/
@RequiredArgsConstructor(staticName = "withArgs")
public class BoltCommand extends CombatCommand {

  private final List<String> splitCommand;

	/*
		Method: getAttackType
		Description: Returns that this command is an Attack
		Output: Attack object
	*/
  @Override
  protected AttackType getAttackType() {
    return BOLT;
  }

	/*
		Method: canSpecialize
		Description: Returns whether this command can be specialized or not
		Output: False
	*/
  @Override
  protected boolean canSpecialize() {
    return false;
  }

	/*
		Method: getCommand
		Description: Returns list with this command
		Output: List object with this command
	*/
  @Override
  protected List<String> getCommand() {
    return splitCommand;
  }
}
