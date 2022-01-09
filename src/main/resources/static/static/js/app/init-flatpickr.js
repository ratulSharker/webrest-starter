"use strict";

function initFlatPickr(selector, flatPickrOptions) {
	$(selector).flatpickr(Object.assign(flatPickrOptions, {
		enableTime: true,
		dateFormat: "Y-m-d h:i K"
	}));
}

function registerDateTimeSubmitModifier(formSelector, dateTimeSelector, hiddenDateTimeSelector) {
	// https://stackoverflow.com/a/2647908/2143128
	$(formSelector).submit(function () {
		$(dateTimeSelector).prop('disabled', true);
		const publishedDateTimeISO = moment($(dateTimeSelector).val(), "YYYY-MM-DD h:mm a").toISOString();
		$(hiddenDateTimeSelector).val(publishedDateTimeISO);
		return true;
	});
}