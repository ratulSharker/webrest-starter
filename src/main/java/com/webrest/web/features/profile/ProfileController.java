package com.webrest.web.features.profile;

import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.webrest.common.entity.AppUser;
import com.webrest.common.interceptor.AuthorizationInterceptor;
import com.webrest.common.service.AppUserService;
import com.webrest.web.common.Alert;
import com.webrest.web.common.Breadcrumb;
import com.webrest.web.common.CookieFlashAttribute;
import com.webrest.web.constants.WebEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ProfileController {

	private final Logger logger = LoggerFactory.getLogger(ProfileController.class);
	private final String PROFILE_UPDATE_RESULT_KEY = "prof-update-res";

	private final AppUserService appUserService;
	private final CookieFlashAttribute cookieFlashAttribute;

	@GetMapping(value = WebEndpoint.MY_PROFILE)
	public String preview(Model model, HttpServletRequest request) {
		AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
		AppUser userMe = appUserService.findById(principleObject.getAppUserId());

		model.addAttribute("user", userMe);
		model.addAttribute("updateProfilePath", WebEndpoint.MY_PROFILE_UPDATE);

		Breadcrumb.builder().addItem("My Profile").build(model);

		return "features/profile/profile-preview";
	}

	@GetMapping(value = WebEndpoint.MY_PROFILE_UPDATE)
	public String getUpdateForm(Model model, HttpServletRequest request, HttpServletResponse response) {

		AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
		AppUser userMe = appUserService.findById(principleObject.getAppUserId());

		try {
			Optional<Map<String, Object>> cookieData = cookieFlashAttribute.getValues(request, response);
			if(cookieData.isPresent()) {
				Boolean isUpdateSuccess = (Boolean) cookieData.get().get(PROFILE_UPDATE_RESULT_KEY);
				if(isUpdateSuccess) {
					Alert.addSuccessAlertAttributeToModel("Success", "Successfully updated profile", model);
				} else {
					Alert.addFailureAlertAttributeToModel("Failure", "Failed to update profile", model);
				}
			}
		} catch(Exception ex) {
			logger.error("Error accessing cookie flash attribute", ex);
		}

		model.addAttribute("user", userMe);
		model.addAttribute("myProfilePath", WebEndpoint.MY_PROFILE);

		Breadcrumb.builder().addItem("My Profile", WebEndpoint.MY_PROFILE).addItem("Update My Profile").build(model);

		return "features/profile/profile-update";
	}

	@PostMapping(value = WebEndpoint.MY_PROFILE_UPDATE)
	public ModelAndView submitUpdateForm(@ModelAttribute("user") AppUser updatedUser, HttpServletRequest request,
			Model model, HttpServletResponse response) {

		try {
			AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
			appUserService.updateOwnProfile(principleObject.getAppUserId(), updatedUser);

			Alert alert = Alert.builder().success(true).title("Success").details("Profile Updated").build();
			model.addAttribute("alert", alert);

			// For updating for the top right corner
			principleObject.setProfilePicturePath(updatedUser.getProfilePicturePath());

			Map<String, Object> cookieData = Map.of(PROFILE_UPDATE_RESULT_KEY, true);
			cookieFlashAttribute.setValues(cookieData, response);

			return new ModelAndView(new RedirectView(WebEndpoint.MY_PROFILE_UPDATE));

		} catch (Exception ex) {
			logger.error("Error During profile update", ex);
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
			model.addAttribute("myProfilePath", WebEndpoint.MY_PROFILE);
			Breadcrumb.builder().addItem("My Profile", WebEndpoint.MY_PROFILE).addItem("Update My Profile").build(model);
			return new ModelAndView("features/profile/profile-update");
		}
	}
}
