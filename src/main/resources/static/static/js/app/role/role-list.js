$(document).ready(function () {

	const dataTable = initDataTable("role-table", "/role-load-data", [
			{
				data: "roleId",
			},
			{
				data: "name"
			},
			{
				orderable: false,
				render: function (data, type, row, meta) {

					// const rowConfiguration = {
					// 	detailsPagePath: row.appUserType == "ADMIN" ? `/admin-user-details/${row.appUserId}` : `/end-user-details/${row.appUserId}`,
					// 	updatePagePath: row.appUserType == "ADMIN" ? `/update-admin-user/${row.appUserId}` : `/update-end-user/${row.appUserId}`
					// };

					if (row.isSuperAdmin === true) {
						return `
                        <div class="d-flex justify-content-center">
                            <div class="dropdown">
                            <a class="dropdown-toggle" href="#" role="button" id="user-manage-dropdown-${row.roleId}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Manage
                            </a>
                            <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="user-manage-dropdown-${row.roleId}">
                                <li><a class="dropdown-item disabled"><span><i class="fas fa-info-circle"></i> Details</span></a></li>
                                <li><a class="dropdown-item disabled"><span><i class="fa fa-edit"></i> Update</span></a></li>
                            </ul>
                            </div>
                        </div>
                        `;
					} else {
						return `
                        <div class="d-flex justify-content-center">
                            <div class="dropdown">
                            <a class="dropdown-toggle" href="#" role="button" id="user-manage-dropdown-${row.roleId}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Manage
                            </a>
                            <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="user-manage-dropdown-${row.appUserId}">
                                <li><a class="dropdown-item text-primary" href="${'#'}"><span><i class="fas fa-info-circle"></i> Details</span></a></li>
                                <li><a class="dropdown-item text-primary" href="${'/update-role/' + row.roleId}"><span><i class="fa fa-edit"></i> Update</span></a></li>
                            </ul>
                            </div>
                        </div>
                        `;
					}


				}
			}
		], "Search role",
		function () {
			return {
			};
		}, 
		{
			format: {
				header: function(header, index) {
					switch (index) {
						default:
							return header;
					}
				}
			}
		});
});