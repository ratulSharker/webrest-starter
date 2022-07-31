package com.webrest.web.features.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webrest.common.dto.datatable.DataTableResponseModel;
import com.webrest.common.entity.AppUser;
import com.webrest.common.enums.AppUserType;
import com.webrest.common.service.AppUserService;
import com.webrest.common.utils.SimpleDataTableHelper;
import com.webrest.web.common.Alert;
import com.webrest.web.common.Breadcrumb;
import com.webrest.web.common.CookieFlashAttribute;
import com.webrest.web.constants.WebEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class AppUserController {

	// TODO: Use lombok's `@Slf4j` annotation
	private final Logger logger = LoggerFactory.getLogger(AppUserController.class);

	private final AppUserService appUserService;
	private final CookieFlashAttribute cookieFlashAttribute;

	@GetMapping(value = WebEndpoint.USER)
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
		cookieFlashAttribute.getValuesAndAddAlertModel(model, request, response);
		Breadcrumb.builder().addItem("All User").build(model);
		return "features/user/user-list";
	}

	@GetMapping(value = WebEndpoint.USER_LOAD_DATA)
	@ResponseBody
	public DataTableResponseModel<AppUser> loadUserData(HttpServletRequest request,
			@RequestParam(name = "appUserType", required = false) AppUserType appUserType) {
		logger.info("App user info found {}", appUserType);
		SimpleDataTableHelper<AppUser> simpleDataTableHelper = SimpleDataTableHelper.<AppUser>builder().request(request)
				.dataSupplier((Pageable pageable, String searchValue) -> {
					return appUserService.filter(pageable, searchValue, appUserType);
				}).build();

		return simpleDataTableHelper.getResponse();
	}

	@GetMapping(value = WebEndpoint.CREATE_USER)
	public String getCreateUserForm(Model model, HttpServletRequest request,
			HttpServletResponse response) {
		AppUser appUser = new AppUser();
		cookieFlashAttribute.getValuesAndAddAlertModel(model, request, response);
		model.addAttribute("appUserForm", appUser);
		Breadcrumb.builder().addItem("Create Admin User").build(model);
		return "features/user/end-user-create";
	}

	@PostMapping(value = WebEndpoint.CREATE_USER)
	public ModelAndView submitCreateUserForm(@ModelAttribute("appUserForm") AppUser appUser,
			BindingResult result, Model model, HttpServletResponse response) {

		try {
			appUser.setAppUserType(AppUserType.END_USER);
			appUserService.createAppUser(appUser);
			cookieFlashAttribute.setAlertValues(true, "Success", "End user creation successful", response);
			return new ModelAndView(new RedirectView(WebEndpoint.CREATE_USER));
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("End user creation failed", ex, model);
			Breadcrumb.builder().addItem("Create Admin User").build(model);
			return new ModelAndView("features/user/end-user-create");
		}
	}

	@GetMapping(value = WebEndpoint.ADMIN_USER_DETAILS)
	public String getAdminDetails(@PathVariable("appUserId") Long appUserId, Model model) {

		try {
			AppUser appUser = appUserService.findById(appUserId);
			model.addAttribute("appUser", appUser);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("Admin User details (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
		}
		return "features/user/admin-user-details";
	}

	@GetMapping(value = WebEndpoint.UPDATE_ADMIN_USER)
	public String getAdminUpdateForm(@PathVariable("appUserId") Long appUserId, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			AppUser appUser = appUserService.findById(appUserId);
			cookieFlashAttribute.getValuesAndAddAlertModel(model, request, response);
			model.addAttribute("appUserForm", appUser);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("Update Admin User (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
		}
		return "features/user/admin-user-update";
	}

	@PostMapping(value = WebEndpoint.UPDATE_ADMIN_USER)
	public ModelAndView submitAdminUpdateForm(@ModelAttribute("appUserForm") AppUser appUser,
			BindingResult bindingResult, @PathVariable("appUserId") Long appUserId, Model model,
			HttpServletResponse response) {

		try {
			appUserService.update(appUserId, appUser);
			cookieFlashAttribute.setAlertValues(true, "Success", "Admin user updated successfully",
					response);
			return new ModelAndView(new RedirectView(WebEndpoint.UPDATE_ADMIN_USER));
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
			return new ModelAndView("features/user/admin-user-update");
		}
	}

	@GetMapping(value = WebEndpoint.END_USER_DETAILS)
	public String getEndUserDetails(@PathVariable("appUserId") Long appUserId, Model model) {
		try {
			AppUser appUser = appUserService.findById(appUserId);
			model.addAttribute("appUser", appUser);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("End User details (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
		}

		return "features/user/end-user-details";
	}

	@GetMapping(value = WebEndpoint.UPDATE_END_USER)
	public String getEndUserUpdateForm(@PathVariable("appUserId") Long appUserId, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			AppUser appUser = appUserService.findById(appUserId);
			cookieFlashAttribute.getValuesAndAddAlertModel(model, request, response);
			model.addAttribute("appUserForm", appUser);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("Update End User (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
		}
		return "features/user/end-user-update";
	}

	@PostMapping(value = WebEndpoint.UPDATE_END_USER)
	public ModelAndView postEndUserUpdateForm(@PathVariable("appUserId") Long appUserId,
			@ModelAttribute("appUserForm") AppUser appUser, BindingResult bindingResult,
			Model model, HttpServletResponse response) {

		try {
			appUserService.update(appUserId, appUser);
			cookieFlashAttribute.setAlertValues(true, "Success", "End user updated successfully",
					response);
			return new ModelAndView(new RedirectView(WebEndpoint.UPDATE_END_USER));
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
			return new ModelAndView("features/user/end-user-update");
		}
	}
}
