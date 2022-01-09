/**
 *
 * @param {Object} request JQuery ajax options
 */

function ajaxPromise(request) {

    return new Promise(function (resolve, reject) {
        FullScreenLoader.show();

        $.ajax(Object.assign({
            beforeSend: function (req) {
                for (const headerKey in request.headers) {
                    req.setRequestHeader(headerKey, request.headers[headerKey]);
                }
                req.setRequestHeader("Authorization", Cookies.get('authorization'));
            },
            success: function (data) {
                FullScreenLoader.hide();
                resolve(data);
            },
            error: function (error) {
                FullScreenLoader.hide();
                toastr.error(error.responseJSON.metadata.error.message, "Failure!", {
                    timeOut: 10000,
                    positionClass: "toast-top-right"
                });

                reject(error);
            }
        }, request));
    });
}