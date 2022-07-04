$(document).ready(function () {

	const dataTable = initDataTable("user-table", "/user-load-data", [
			{
				data: "appUserId",
			},
			{
				data: "name"
			},
			{
				data: "mobile"
			},
			{
				data: "email"
			},
			{
				orderable: false,
				data: "appUserType"
			},
			{
				orderable: false,
				render: function (data, type, row, meta) {

					const rowConfiguration = {
						detailsPagePath: row.appUserType == "ADMIN" ? `/admin-user-details/${row.appUserId}` : `/end-user-details/${row.appUserId}`,
						updatePagePath: row.appUserType == "ADMIN" ? `/update-admin-user/${row.appUserId}` : `/update-end-user/${row.appUserId}`
					};

					if (row.appUserType === "END_USER") {
						return `
                        <div class="d-flex justify-content-center">
                            <div class="dropdown">
                            <a class="dropdown-toggle" href="#" role="button" id="user-manage-dropdown-${row.appUserId}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Manage
                            </a>
                            <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="user-manage-dropdown-${row.appUserId}">
                                <li><a class="dropdown-item text-primary" href="${rowConfiguration.detailsPagePath}"><span><i class="fas fa-info-circle"></i> Details</span></a></li>
                                <li><a class="dropdown-item text-primary" href="${rowConfiguration.updatePagePath}"><span><i class="fa fa-edit"></i> Update</span></a></li>
                            </ul>
                            </div>
                        </div>
                        `;
					} else {
						return `
                        <div class="d-flex justify-content-center">
                            <div class="dropdown">
                            <a class="dropdown-toggle" href="#" role="button" id="user-manage-dropdown-${row.appUserId}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Manage
                            </a>
                            <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="user-manage-dropdown-${row.appUserId}">
                                <li><a class="dropdown-item text-primary" href="${rowConfiguration.detailsPagePath}"><span><i class="fas fa-info-circle"></i> Details</span></a></li>
                                <li><a class="dropdown-item text-primary" href="${rowConfiguration.updatePagePath}"><span><i class="fa fa-edit"></i> Update</span></a></li>
                            </ul>
                            </div>
                        </div>
                        `;
					}


				}
			}
		], "Search name, mobile or email",
		function () {
			return {
				"appUserType": $("#user-type-select").val()
			};
		}, 
		{
			format: {
				header: function(header, index) {
					switch (index) {
						case 4:
							return "User Type";
						default:
							return header;
					}
				}
			}
		});

	$("#user-type-select").on("change", function () {
		dataTable.ajax.reload();
	});
});