$(document).ready(function () {

    const lastSelectedMenuPathKey = "last-selected-menu-key";

    // When clicking a menu, then
    // store the last clicked menu path
    $(".menu-link").on("click", function () {
        let clickedPath = $(this).attr("href");
        Cookies.set(lastSelectedMenuPathKey, clickedPath, {
            path: window.GLOBAL_CONSTANTS.context_path
        });
    });

    // paths, which have no menu entries `/profile`, `update-profile`
    $(".no-menu-group").on("click", function () {
        Cookies.remove(lastSelectedMenuPathKey, {
            path: window.GLOBAL_CONSTANTS.context_path
        });
    });

    // Opening the selected menu
    let lastSelectedMenuPath = Cookies.get(lastSelectedMenuPathKey);
    if (lastSelectedMenuPath == undefined || lastSelectedMenuPath != window.location.pathname) {
		// If the window.location.pathname exists in sidebar, then try to select it
		let sideMenuLinks = $(`a[href='${window.location.pathname}'].menu-link`);
		if(sideMenuLinks.length > 0) {
			lastSelectedMenuPath = window.location.pathname;
			Cookies.set(lastSelectedMenuPathKey, lastSelectedMenuPath, {
				path: window.GLOBAL_CONSTANTS.context_path
			});
		}
    }

    let links = $(`a[href='${lastSelectedMenuPath}'].menu-link`);

    if (links.length > 0 && Cookies.get(lastSelectedMenuPathKey) == undefined) {
        Cookies.set(lastSelectedMenuPathKey, lastSelectedMenuPath, {
            path: window.GLOBAL_CONSTANTS.context_path
        });
    }
    
    links.each(function () {
        $(this).addClass("disabled active font-weight-bold");
        $(this).parent().collapse("show");
        $(this).parents(".menu-group").find(".menu-expander").addClass("active font-weight-bold");
    });

});