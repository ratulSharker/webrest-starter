"use strict";

$(document).ready(function() {
    // Handle owner image upload
    registerFileUploadFlow("#upload-end-user-picture-button",
        "#end-user-profile-image-file",
        "#profilePicturePath",
        "#profile-picture-image");
});