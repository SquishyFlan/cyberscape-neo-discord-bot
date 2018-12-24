package com.titaniumtemplar.discordbot.model.character;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Skill {

	private int ranks;
	private String spec1Name;
	private int spec1Ranks;
	private String spec2Name;
	private int spec2Ranks;

	// Derived values
	private int nextRankCost;
	private boolean spec1Available;
	private int nextSpec1RankCost;
	private boolean spec2Available;
	private int nextSpec2RankCost;

	public void apply(Skill diff) {
		ranks += diff.getRanks();
		spec1Ranks += diff.getSpec1Ranks();
		spec2Ranks += diff.getSpec2Ranks();
		if (diff.getSpec1Name() != null && diff.getSpec1Ranks() > 0) {
			spec1Name = diff.getSpec1Name();
		}
		if (diff.getSpec2Name() != null && diff.getSpec2Ranks() > 0) {
			spec2Name = diff.getSpec2Name();
		}
	}
}
