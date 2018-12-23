// Handle show/display plus/minus butans
// When they're updated, fiddle with the CharSkillsUpdate object
// on Document ready, attach listeners to all the butans
// Show/hide reset butan
// Include formula to handle "next point" costs
var tempCharJson;
var tempChar;
var tempSpLeft;

function handleStatButtons() {
  
}

$(document).ready(function () {
  tempCharJson = JSON.stringify(originalChar);
  tempChar = JSON.parse(tempCharJson);

  handleStatButtons();
});
