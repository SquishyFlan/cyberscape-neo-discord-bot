package com.titaniumtemplar.discordbot.discord.commands;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.combat.Combat;
import com.titaniumtemplar.discordbot.model.exception.NoSuchCharacterException;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import static com.titaniumtemplar.discordbot.model.combat.Attack.AttackType.ATTACK;

@RequiredArgsConstructor(staticName = "withArgs")
public class AttackCommand implements DiscordCommand {

  private final String[] splitCommand;

  @Override
  public void run(
      CyberscapeService service,
      Myra myra,
      Message message,
      User author,
      Member member) {
    if (member == null) {
      member = myra.getMember(author);
      if (member == null) {
      author.openPrivateChannel()
	  .queue((channel) -> channel.sendMessage("I'm sorry, you don't seem to be connected to any Cyberscape Neo compatible server!").queue());
      }
    }
    Combat combat = myra.getCombat(member);
    if (combat == null) {
      author.openPrivateChannel()
	  .queue((channel) -> channel.sendMessage("I'm sorry, I couldn't find any combat for you to join!").queue());
    } else {
      try {
	CharStats character = service.getCharacter(author.getId());
        character.setName(member.getEffectiveName());
	synchronized (combat) {
	  combat.addAttack(character, ATTACK, null);
	}
	myra.updateCombatMessage(combat);
      } catch (NoSuchCharacterException ex) {
	author.openPrivateChannel()
	    .queue((channel) -> channel.sendMessage("I'm sorry, I couldn't find your character. Please use \".register\" to join!").queue());
      }

    }

    message.delete().queue();
  }

}
