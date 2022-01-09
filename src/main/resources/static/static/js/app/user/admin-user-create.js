"use strict";

$(document).ready(function () {
    // Handle owner image upload
    registerFileUploadFlow("#upload-admin-user-picture-button",
        "#admin-user-profile-image-file",
        "#profilePicturePath",
        "#profile-picture-image");
});