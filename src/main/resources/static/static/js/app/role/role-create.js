$(document).ready(function() {

	// Masonary initialization
	$('.grid').masonry({
		itemSelector: '.grid-item',
		percentPosition: true,
	});

	// All feature checkbox click listener
	$("#authorization-card").on("click", "input[type='checkbox'][id$='-feature-checkbox']", function () {
		let feature = $(this);
		let featureChecked = feature.prop("checked");
		feature.closest(".card").find("input[type='checkbox']").prop("checked", featureChecked);
	});

	// Features all action checkbox click listener
	$("#authorization-card").on("click", "input[type='checkbox'][id$='-action-checkbox']", function () {
		let action = $(this);
		let featureCard = action.closest(".card");
		let numberOfCheckedAction = featureCard.find("input[type='checkbox'][id$='-action-checkbox']:checked").length;
		let numberOfAllActions = featureCard.find("input[type='checkbox'][id$='-action-checkbox']").length;
		let feature = featureCard.find("input[type='checkbox'][id$='-feature-checkbox']");
		if (numberOfCheckedAction == numberOfAllActions) {
			feature.prop("checked", true);
		} else {
			feature.prop("checked", false);
		}
	});
	
});