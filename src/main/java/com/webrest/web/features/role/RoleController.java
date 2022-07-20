package com.webrest.web.features.role;

import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.webrest.common.dto.datatable.DataTableResponseModel;
import com.webrest.common.entity.Role;
import com.webrest.common.service.RoleService;
import com.webrest.common.utils.SimpleDataTableHelper;
import com.webrest.web.constants.WebEndpoint;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RoleController {
	
	private final RoleService roleService;

	@GetMapping(WebEndpoint.ROLE)
	public String list() {
		return "features/role/role-list";
	}

	@GetMapping(WebEndpoint.ROLE_LOAD_DATA)
	@ResponseBody
	public DataTableResponseModel<Role> loadRoleData(HttpServletRequest request) {
		SimpleDataTableHelper<Role> simpleDataTableHelper = SimpleDataTableHelper.<Role>builder()
				.request(request).dataSupplier((Pageable pageable, String searchValue) -> {
					return roleService.filter(pageable, searchValue);
				}).build();
		return simpleDataTableHelper.getResponse();
	}

	@GetMapping(WebEndpoint.CREATE_ROLE)
	public String getCreateRoleForm(Model model) {

		model.addAttribute("roleForm", new Role());

		return "features/role/role-create";
	}
}
