$(document).ready(function () {

    const lastSelectedMenuPathKey = "last-selected-menu-key";

    // When clicking a menu, then
    // store the last clicked menu path
    $(".menu-link").on("click", function () {
        let clickedPath = $(this).attr("href");
        Cookies.set(lastSelectedMenuPathKey, clickedPath);
    });

    // paths, which have no menu entries `/profile`, `update-profile`
    $(".no-menu-group").on("click", function () {
        Cookies.remove(lastSelectedMenuPathKey);
    });


    // Opening the selected menu
    let lastSelectedMenuPath = Cookies.get(lastSelectedMenuPathKey);
    if (lastSelectedMenuPath == undefined) {
        // first time, when no cookie is set.
        lastSelectedMenuPath = window.location.pathname;
    }

    let links = $(`a[href='${lastSelectedMenuPath}'].menu-link`);

    if (links.length > 0 && Cookies.get(lastSelectedMenuPathKey) == undefined) {
        Cookies.set(lastSelectedMenuPathKey, lastSelectedMenuPath);
    }
    
    links.each(function () {
        $(this).addClass("disabled active font-weight-bold");
        $(this).parent().collapse("show");
        $(this).parents(".menu-group").find(".menu-expander").addClass("active font-weight-bold");
    });

});