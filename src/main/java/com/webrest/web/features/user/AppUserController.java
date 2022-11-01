package com.webrest.web.features.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webrest.common.dto.datatable.DataTableResponseModel;
import com.webrest.common.entity.AppUser;
import com.webrest.common.entity.Role;
import com.webrest.common.service.AppUserService;
import com.webrest.common.service.RoleService;
import com.webrest.common.utils.SimpleDataTableHelper;
import com.webrest.web.common.Alert;
import com.webrest.web.common.Breadcrumb;
import com.webrest.web.common.CookieFlashAttribute;
import com.webrest.web.constants.WebRoutes;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class AppUserController {

	private final AppUserService appUserService;
	private final CookieFlashAttribute cookieFlashAttribute;
	private final RoleService roleService;

	@GetMapping(value = WebRoutes.USER)
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
		cookieFlashAttribute.getValuesAndAddAlertModel(model, request, response);
		Breadcrumb.builder().addItem("All User").build(model);
		return "features/user/user-list";
	}

	@GetMapping(value = WebRoutes.USER_LOAD_DATA)
	@ResponseBody
	public DataTableResponseModel<AppUser> loadUserData(HttpServletRequest request) {
		
		SimpleDataTableHelper<AppUser> simpleDataTableHelper = SimpleDataTableHelper.<AppUser>builder().request(request)
				.dataSupplier((Pageable pageable, String searchValue) -> {
					return appUserService.filter(pageable, searchValue);
				}).build();

		return simpleDataTableHelper.getResponse();
	}

	@GetMapping(value = WebRoutes.CREATE_USER)
	public String getCreateUserForm(Model model, HttpServletRequest request,
			HttpServletResponse response) {
		AppUser appUser = new AppUser();
		cookieFlashAttribute.getValuesAndAddAlertModel(model, request, response);
		model.addAttribute("appUserForm", appUser);
		List<Role> roles = roleService.getActiveRoles();
		model.addAttribute("roles", roles);
		Breadcrumb.builder().addItem("Create User").build(model);
		return "features/user/user-create";
	}

	@PostMapping(value = WebRoutes.CREATE_USER)
	public ModelAndView submitCreateUserForm(@ModelAttribute("appUserForm") AppUser appUser,
			BindingResult result, Model model, HttpServletResponse response) {

		try {
			appUserService.createAppUser(appUser);
			cookieFlashAttribute.setAlertValues(true, "Success", "User creation successful", response);
			return new ModelAndView(new RedirectView(WebRoutes.CREATE_USER));
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("User creation failed", ex, model);
			Breadcrumb.builder().addItem("Create User").build(model);
			return new ModelAndView("features/user/user-create");
		}
	}

	@GetMapping(value = WebRoutes.USER_DETAILS)
	public String getEndUserDetails(@PathVariable("appUserId") Long appUserId, Model model) {
		try {
			AppUser appUser = appUserService.findByIdWithRoles(appUserId);
			model.addAttribute("appUser", appUser);
			Breadcrumb.builder().addItem("All Users", WebRoutes.USER)
					.addItem(String.format("User details (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
		}

		return "features/user/user-details";
	}

	@GetMapping(value = WebRoutes.UPDATE_USER)
	public String getEndUserUpdateForm(@PathVariable("appUserId") Long appUserId, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			AppUser appUser = appUserService.findByIdWithRoles(appUserId);
			cookieFlashAttribute.getValuesAndAddAlertModel(model, request, response);
			model.addAttribute("appUserForm", appUser);
			List<Role> roles = roleService.getActiveRoles();
			model.addAttribute("roles", roles);
			Breadcrumb.builder().addItem("All Users", WebRoutes.USER)
					.addItem(String.format("Update User (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
		}
		return "features/user/user-update";
	}

	@PostMapping(value = WebRoutes.UPDATE_USER)
	public ModelAndView postEndUserUpdateForm(@PathVariable("appUserId") Long appUserId,
			@ModelAttribute("appUserForm") AppUser appUser, BindingResult bindingResult,
			Model model, HttpServletResponse response) {

		try {
			appUserService.update(appUserId, appUser);
			cookieFlashAttribute.setAlertValues(true, "Success", "User Updated Successfully",
					response);
			return new ModelAndView(new RedirectView(WebRoutes.UPDATE_USER));
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
			return new ModelAndView("features/user/user-update");
		}
	}
}
