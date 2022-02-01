package com.webrest.web.features.user;

import java.util.Map;
import java.util.Optional;

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

@Controller
public class AppUserController {

	private final Logger logger = LoggerFactory.getLogger(AppUserController.class);
	private final AppUserService appUserService;
	private final CookieFlashAttribute cookieFlashAttribute;

	private final String SUCCESS_OR_FAILED_OPERATION_KEY = "res-stat-key";
	private final String SUCCESS_OR_FAILED_MESSAGE_KEY = "res-msg-key";

	public AppUserController(AppUserService appUserService, CookieFlashAttribute cookieFlashAttribute) {
		this.appUserService = appUserService;
		this.cookieFlashAttribute = cookieFlashAttribute;
	}

	@GetMapping(value = WebEndpoint.USER)
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) {

		try {
			Optional<Map<String, Object>> optionalValues = cookieFlashAttribute.getValues(request, response);

			if (optionalValues.isPresent()) {
				boolean isSuccess = (boolean) optionalValues.get().get(SUCCESS_OR_FAILED_OPERATION_KEY);
				String message = (String) optionalValues.get().get(SUCCESS_OR_FAILED_MESSAGE_KEY);

				Alert alert = Alert.builder().success(isSuccess).title(isSuccess ? "Success" : "Failure")
						.details(message).build();
				model.addAttribute("alert", alert);
			}

		} catch (Exception ex) {
			logger.error("Cookie flash attr de serialization error", ex);
		}

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

	@GetMapping(value = WebEndpoint.CREATE_ADMIN_USER)
	public String getCreateAdminForm(Model model) {
		AppUser appUser = new AppUser();
		model.addAttribute("appUserForm", appUser);
		Breadcrumb.builder().addItem("Create Admin User").build(model);
		return "features/user/admin-user-create";
	}

	// TODO: After successful creation, should redirect to `WebEndpoint.CREATE_ADMIN_USER` with cookies flash attribute
	@PostMapping(value = WebEndpoint.CREATE_ADMIN_USER)
	public String submitCreateAdminForm(@ModelAttribute("appUserForm") AppUser appUser, Model model) {

		try {
			appUser.setAppUserType(AppUserType.ADMIN);
			appUserService.createAppUser(appUser);

			Alert alert = Alert.builder().success(true).title("Successful").details("Admin user creation successful")
					.build();
			model.addAttribute("alert", alert);
			model.addAttribute("appUserForm", new AppUser());

		} catch (Exception ex) {

			Alert alert = Alert.builder().success(false).title("Admin user creation failed").details(ex.getMessage())
					.build();
			model.addAttribute("alert", alert);
		}

		Breadcrumb.builder().addItem("Create Admin User").build(model);
		return "features/user/admin-user-create";
	}

	@GetMapping(value = WebEndpoint.CREATE_END_USER)
	public String getCreateEndUserForm(Model model) {
		AppUser appUser = new AppUser();
		model.addAttribute("appUserForm", appUser);
		Breadcrumb.builder().addItem("Create Admin User").build(model);
		return "features/user/end-user-create";
	}

	// TODO: After successful creation, should redirect to `WebEndpoint.CREATE_END_USER` with cookies flash attribute
	@PostMapping(value = WebEndpoint.CREATE_END_USER)
	public String submitCreateEndUser(@ModelAttribute("appUserForm") AppUser appUser, BindingResult result,
			Model model) {

		try {
			appUser.setAppUserType(AppUserType.END_USER);
			appUserService.createAppUser(appUser);

			Alert alert = Alert.builder().success(true).title("Success").details("End user created").build();
			model.addAttribute("alert", alert);
			model.addAttribute("appUserForm", new AppUser());

		} catch (Exception ex) {
			Alert alert = Alert.builder().success(false).title("End user creation failed")
					.details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}
		Breadcrumb.builder().addItem("Create Admin User").build(model);
		return "features/user/end-user-create";
	}

	@GetMapping(value = WebEndpoint.ADMIN_USER_DETAILS)
	public String getAdminDetails(@PathVariable("appUserId") Long appUserId, Model model) {

		try {
			AppUser appUser = appUserService.findById(appUserId);
			model.addAttribute("appUser", appUser);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("Admin User details (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}
		return "features/user/admin-user-details";
	}

	@GetMapping(value = WebEndpoint.UPDATE_ADMIN_USER)
	public String getAdminUpdateForm(@PathVariable("appUserId") Long appUserId, Model model) {
		try {
			AppUser appUser = appUserService.findById(appUserId);
			model.addAttribute("appUserForm", appUser);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("Update Admin User (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}
		return "features/user/admin-user-update";
	}

	@PostMapping(value = WebEndpoint.UPDATE_ADMIN_USER)
	public String submitAdminUpdateForm(@ModelAttribute("appUserForm") AppUser appUser, BindingResult bindingResult,
			@PathVariable("appUserId") Long appUserId, Model model) {

		try {
			appUserService.update(appUserId, appUser);
			Alert alert = Alert.builder().success(true).title("Success").details("Admin user updated successfully")
					.build();
			model.addAttribute("alert", alert);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("Update Admin User (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}

		return "features/user/admin-user-update";
	}

	@GetMapping(value = WebEndpoint.END_USER_DETAILS)
	public String getEndUserDetails(@PathVariable("appUserId") Long appUserId, Model model) {
		try {
			AppUser appUser = appUserService.findById(appUserId);
			model.addAttribute("appUser", appUser);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("End User details (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}

		return "features/user/end-user-details";
	}

	@GetMapping(value = WebEndpoint.UPDATE_END_USER)
	public String getEndUserUpdateForm(@PathVariable("appUserId") Long appUserId, Model model) {
		try {
			AppUser appUser = appUserService.findById(appUserId);
			model.addAttribute("appUserForm", appUser);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("Update End User (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}
		return "features/user/end-user-update";
	}

	@PostMapping(value = WebEndpoint.UPDATE_END_USER)
	public String postEndUserUpdateForm(@PathVariable("appUserId") Long appUserId,
			@ModelAttribute("appUserForm") AppUser appUser, BindingResult bindingResult, Model model) {

		try {
			appUserService.update(appUserId, appUser);
			Alert alert = Alert.builder().success(true).title("Success")
					.details("End user updated successfully").build();
			model.addAttribute("alert", alert);
			Breadcrumb.builder().addItem("All Users", WebEndpoint.USER)
					.addItem(String.format("Update End User (%s)", appUser.getEmail())).build(model);
		} catch (Exception ex) {
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}

		return "features/user/end-user-update";
	}
}
