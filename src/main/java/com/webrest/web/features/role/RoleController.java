package com.webrest.web.features.role;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webrest.common.dto.datatable.DataTableResponseModel;
import com.webrest.common.entity.Role;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.service.RoleService;
import com.webrest.common.utils.SimpleDataTableHelper;
import com.webrest.web.common.Alert;
import com.webrest.web.common.Breadcrumb;
import com.webrest.web.common.CookieFlashAttribute;
import com.webrest.web.constants.WebEndpoint;

import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RoleController {
	
	private final RoleService roleService;
	private final CookieFlashAttribute cookieFlashAttribute;

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
	public String getCreateRoleForm(Model model, HttpServletRequest request, HttpServletResponse response) {

		model.addAttribute("roleForm", new Role());

		List<Pair<AuthorizedFeature, List<AuthorizedAction>>> assignableFeaturesWithActions = roleService
				.getAssignableFeaturesWithAction();
		model.addAttribute("assignableFeaturesWithActions", assignableFeaturesWithActions);
		Breadcrumb.builder().addItem("Create Role").build(model);
		cookieFlashAttribute.getValuesAndAddAlertModel(model, request, response);

		return "features/role/role-create";
	}

	@PostMapping(WebEndpoint.CREATE_ROLE)
	public ModelAndView submitCreateRoleForm(@ModelAttribute("roleForm") Role role, HttpServletResponse response,
			Model model) {

		try {
			roleService.createNewRole(role);
			String message = String.format("Role created with name `%s`", role.getName());
			cookieFlashAttribute.setAlertValues(true, "Success", message, response);
			return new ModelAndView(new RedirectView(WebEndpoint.CREATE_ROLE));
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Role creation failed", ex, model);
			Breadcrumb.builder().addItem("Create Role").build(model);

			List<Pair<AuthorizedFeature, List<AuthorizedAction>>> assignableFeaturesWithActions = roleService
					.getAssignableFeaturesWithAction();
			model.addAttribute("assignableFeaturesWithActions", assignableFeaturesWithActions);

			return new ModelAndView("features/role/role-create");
		}
	}
}
