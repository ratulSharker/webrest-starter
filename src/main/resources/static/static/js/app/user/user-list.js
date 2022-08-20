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
				render: function (data, type, row, meta) {
					return `
					<div class="d-flex justify-content-center">
						<div class="dropdown">
							<a class="dropdown-toggle" href="#" role="button" id="user-manage-dropdown-${row.appUserId}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
								Manage
							</a>
							<ul class="dropdown-menu dropdown-menu-right" aria-labelledby="user-manage-dropdown-${row.appUserId}">
								<li><a class="dropdown-item text-primary" href="${"/user-details/" + row.appUserId}"><span><i class="fas fa-info-circle"></i> Details</span></a></li>
								<li><a class="dropdown-item text-primary" href="${"/update-user/" + row.appUserId}"><span><i class="fa fa-edit"></i> Update</span></a></li>
							</ul>
						</div>
					</div>
					`;
				}
			}
		], "Search name, mobile or email",
		function () {
			return {
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
});