function uploadFilePromise(file) {

    let formData = new FormData();
    formData.append("file", file);

    return ajaxPromise({
        type: "POST",
        url: "/api/v1/upload",
        data: formData,
        contentType: false,
        processData: false
    });
}

function registerFileUploadFlow(uploadInitiatorSelector, hiddenInputFileSelector, hiddenFormInputSelector, previewImageSelector) {

    $(uploadInitiatorSelector).on("click", function () {
        $(hiddenInputFileSelector).click();
    });

    $(hiddenInputFileSelector).on("change", async function () {
        if (this.files.length > 0) {

            if (this.files[0].size > 102400) {
                toastr.error("File must be under 100KB.", "Failure!", {
                    timeOut: 10000,
                    positionClass: "toast-top-right"
                });
                return;
            }

            try {
                const result = await uploadFilePromise(this.files[0]);
                console.log("File upload success");
                $(hiddenFormInputSelector).val(result.data.tempFilename);

                $(previewImageSelector).attr("src", URL.createObjectURL(this.files[0]));
            } catch (err) {
                console.log(err);
            }
        }
    });
}