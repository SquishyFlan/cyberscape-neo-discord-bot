function forceCombat() {
	var guildId = $(this).attr("data-guild-id");
	$.ajax({
		url: "api/forceCombat",
		type: "POST",
		data: JSON.stringify({gid:guildId}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (response) {
			window.location = window.location;
		}
	});
}

$(document).ready(function () {
	$("input.forceCombat").on("click", forceCombat);
});