package com.titaniumtemplar.discordbot.discord.commands;

import static com.titaniumtemplar.discordbot.model.combat.AttackType.SHOOT;

import com.titaniumtemplar.discordbot.model.combat.AttackType;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "withArgs")
public class ShootCommand extends CombatCommand {

  private final List<String> splitCommand;

  @Override
  protected AttackType getAttackType() {
    return SHOOT;
  }

  @Override
  protected boolean canSpecialize() {
    return false;
  }

  @Override
  protected List<String> getCommand() {
    return splitCommand;
  }
}
