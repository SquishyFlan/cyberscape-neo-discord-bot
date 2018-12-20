package com.titaniumtemplar.discordbot.model.combat;

import com.titaniumtemplar.discordbot.model.monster.Monster;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.joining;

@Getter
@RequiredArgsConstructor
public class Round
{
  private final int number;
  private final List<Attack> attacks = new ArrayList<>();
  private final Set<String> participants = new HashSet<>();

  String resolve(Monster monster) {
    if (attacks.isEmpty())
      return monster.getName() + " starts to look restless...";

    String roundString = attacks.stream()
	.peek(monster::applyAttack)
	.map(Attack::getDamageString)
	.collect(joining("\n"));

    monster.handleShields();

    return roundString;
  }

  void addAttack(String participantId, Attack attack) {
    if (participants.contains(participantId)) {
      return;
    }
    participants.add(participantId);
    attacks.add(attack);
  }

  public boolean isEmpty() {
    return attacks.isEmpty();
  }
}
