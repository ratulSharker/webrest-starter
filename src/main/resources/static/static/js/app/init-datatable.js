function initDataTable(tableId, url, columns, searchPlaceholder, extraParamsCallback, excelExportOptions) {

    // init the data table
    let dataTable = $(`#${tableId}`).DataTable({
		order: [[ 0, "desc" ]],
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
		buttons: [
			{
				// https://datatables.net/reference/button/excelHtml5
				extend: "excelHtml5",
				text: "<i class='fas fa-file-download'></i> Excel",
				className: "btn btn-primary btn-sm ml-2",

				// https://jsfiddle.net/saurabhmisra87/hL6xw1sk/18/
				exportOptions: Object.assign({
					columns: ":not(:last-child)"
				}, excelExportOptions || {}),
				createEmptyCells : true
			}
		],
		lengthMenu: [
			10, 50, 100, 500, 1000
		],
		// https://datatables.net/reference/option/dom#Styling
		dom: "<'row'<'col-sm-12 col-md-6 d-flex'lB><'col-sm-12 col-md-6'f>>" +
		"<'row'<'col-sm-12'tr>>" +
		"<'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>"

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