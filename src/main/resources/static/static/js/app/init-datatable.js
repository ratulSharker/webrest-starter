function initDataTable(tableId, url, columns, searchPlaceholder, extraParamsCallback) {

    // init the data table
    let dataTable = $(`#${tableId}`).DataTable({
        ordering: false,
        processing: false,
        serverSide: true,
        searchHighlight: true,
        ajax: async function (params, callback) {
            const response = await ajaxPromise({
                url: url,
                data: Object.assign(params, extraParamsCallback()),
                dataType: "json",
                type: "get"
            });
            callback(response);
        },
        columns: columns,
        language: {
            searchPlaceholder: searchPlaceholder,
            search: '<i class="fa fa-search" aria-hidden="true"></i>'
        },

    });

    // initiate search on `enter` key
    $(`#${tableId}_wrapper input[type=search]`).unbind();
    $(`#${tableId}_wrapper input[type=search]`).on("keyup", function (event) {
        let enterKeyCode = 13;

        if (event.keyCode == enterKeyCode) {
            dataTable.search(this.value).draw();
        }

    });

    // setting search bar width
    $(`#${tableId}_wrapper input[type="search"]`).css({
        'width': '300px',
        'display': 'inline-block'
    });

    return dataTable;
}