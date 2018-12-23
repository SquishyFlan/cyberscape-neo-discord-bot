var tempCharJson;
var tempChar;
var tempSpLeft;
var skillDiff = {};
var inRequest = false;

function handleSkillButtons() {
	var anyPointSpent = false;
	$.each(tempChar.skills, function (skillType, skill) {
		$("#" + skillType + "Cost").text("(Next: " + skill.nextRankCost + ")");
		$("#" + skillType + "Spec1Cost").text("(Next: " + skill.nextSpec1RankCost + ")");
		$("#" + skillType + "Spec2Cost").text("(Next: " + skill.nextSpec2RankCost + ")");

		// We can afford a skill point, show plus butan
		if (skill.nextRankCost <= tempChar.spLeft) {
			$("#" + skillType + "Increment").removeClass("hidden");
		} else {
			$("#" + skillType + "Increment").addClass("hidden");
		}

		// We have spent a skill point, show minus butan and save and reset butans
		if (skillDiff[skillType] !== undefined && skillDiff[skillType].ranks) {
			$("#" + skillType + "Decrement").removeClass("hidden");
			anyPointSpent = true;
		} else {
			$("#" + skillType + "Decrement").addClass("hidden");
		}

		// We can afford a spec point, show plus butan
		if (skill.nextSpec1RankCost && skill.nextSpec1RankCost <= tempChar.spLeft
						&& skill.spec1Ranks < skill.ranks) {
			$("#" + skillType + "Spec1Increment").removeClass("hidden");
			if (skill.spec1Available) {
				$("#" + skillType + "Spec1Name").removeClass("hidden");
			} else {
				$("#" + skillType + "Spec1Name").addClass("hidden");
			}
		} else {
			$("#" + skillType + "Spec1Increment").addClass("hidden");
			$("#" + skillType + "Spec1Name").addClass("hidden");
		}

		// We have spent a spec point, show minus butan
		if (skillDiff[skillType] !== undefined && skillDiff[skillType].spec1Ranks) {
			$("#" + skillType + "Spec1Decrement").removeClass("hidden");
			anyPointSpent = true;
		} else {
			$("#" + skillType + "Spec1Decrement").addClass("hidden");
		}

		// We can afford a spec point, show plus butan
		if (skill.nextSpec2RankCost && skill.nextSpec2RankCost <= tempChar.spLeft
						&& skill.spec2Ranks < skill.ranks) {
			$("#" + skillType + "Spec2Increment").removeClass("hidden");
			if (skill.spec2Available) {
				$("#" + skillType + "Spec2Name").removeClass("hidden");
			} else {
				$("#" + skillType + "Spec2Name").addClass("hidden");
			}
		} else {
			$("#" + skillType + "Spec2Increment").addClass("hidden");
			$("#" + skillType + "Spec2Name").addClass("hidden");
		}

		// We have spent a spec point, show minus butan
		if (skillDiff[skillType] !== undefined && skillDiff[skillType].spec2Ranks) {
			$("#" + skillType + "Spec2Decrement").removeClass("hidden");
			anyPointSpent = true;
		} else {
			$("#" + skillType + "Spec2Decrement").addClass("hidden");
		}
	});

	if (anyPointSpent) {
		// TODO: Put these in a div to hide at the same time, duh
		$("#save").removeClass("hidden");
		$("#reset").removeClass("hidden");
	}
}

function incrementSkill() {
	if (inRequest) {
		return false;
	}

	var skillType = $(this).attr("data-skill-type");
	if (!skillDiff[skillType])
		skillDiff[skillType] = {};
	var skill = skillDiff[skillType];

	var skillField = $(this).attr("data-skill-field");
	if (!skill[skillField])
		skill[skillField] = 0;
	skill[skillField]++;
	recalcStats();
}

function decrementSkill() {
	if (inRequest) {
		return false;
	}

	var skillType = $(this).attr("data-skill-type");
	var skillField = $(this).attr("data-skill-field");
	skillDiff[skillType][skillField]--;
	recalcStats();
}

function recalcStats() {
	if (inRequest) {
		return;
	}
	inRequest = true;
	$.ajax({
		url: "api/statCheck",
		type: "POST",
		data: JSON.stringify({skills:skillDiff}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (response) {
			tempChar = response;
			inRequest = false;
			redrawStats();
		}
	});
}

function redrawStats() {
	// Redraw Vitals
	$("#hpCurrent").text(tempChar.hpCurrent);
	$("#hpMax").text(tempChar.hpMax);
	$("#mpCurrent").text(tempChar.mpCurrent);
	$("#mpMax").text(tempChar.mpMax);
	$("#spCurrent").text(tempChar.spUsed);
	$("#spMax").text(tempChar.spTotal);

	// Redraw stats
	$.each(tempChar.stats, function (statType, statValue) {
		if (statType === "int_") statType = "int";
		$("#" + statType + "Value").text(statValue);
	});

	// Redraw skills
	$.each(tempChar.skills, function (skillType, skill) {
		$("#" + skillType + "Value").text(skill.ranks);
		$("#" + skillType + "Cost").text(skill.nextRankCost);
		$("#" + skillType + "Spec1Ranks").text(skill.spec1Ranks);
		$("#" + skillType + "Spec1Cost").text(skill.nextSpec1RankCost);
		$("#" + skillType + "Spec2Ranks").text(skill.spec2Ranks);
		$("#" + skillType + "Spec2Cost").text(skill.nextSpec2RankCost);
	});

	handleSkillButtons();
}

function saveChar() {
	if (inRequest) {
		return;
	}
	inRequest = true;
	$.ajax({
		url: "api/character",
		type: "PUT",
		data: JSON.stringify({skills:skillDiff}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function () {
			tempCharJson = JSON.stringify(tempChar);
			skillDiff = {};
			inRequest = false;
			handleSkillButtons();
		}
	});
	// Busify Save button, prevent double-click
	// Ajax request to save character
	// Rewrite originalChar with cloned tempChar
}

function reset() {
	tempChar = JSON.parse(tempCharJson);
	recalcStats();
}

$(document).ready(function () {
	if (readOnly) {
		return;
	}

	tempCharJson = JSON.stringify(originalChar);
	tempChar = JSON.parse(tempCharJson);

	$("input.dec").on("click", decrementSkill);
	$("input.inc").on("click", incrementSkill);
	$("input#save").on("click", saveChar);
	$("input#reset").on("click", reset);
	// TODO: Make specialization name boxen red if they're empty onBlur, and apply them to the diff

	handleSkillButtons();
});
