"use strict";

$(document).ready(function() {
    // Handle owner image upload
    registerFileUploadFlow("#upload-user-picture-button",
        "#user-profile-image-file",
        "#profilePicturePath",
        "#profile-picture-image");
});