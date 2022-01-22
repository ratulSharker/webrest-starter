package com.webrest.web.profile;

import javax.servlet.http.HttpServletRequest;

import com.webrest.common.entity.AppUser;
import com.webrest.common.interceptor.AuthorizationInterceptor;
import com.webrest.common.service.AppUserService;
import com.webrest.web.common.Alert;
import com.webrest.web.constants.WebEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

	private Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	private AppUserService appUserService;

	@GetMapping(value = WebEndpoint.MY_PROFILE)
	public String preview(Model model, HttpServletRequest request) {
		AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
		AppUser userMe = appUserService.findById(principleObject.getAppUserId());

		model.addAttribute("user", userMe);
		model.addAttribute("updateProfilePath", WebEndpoint.MY_PROFILE_UPDATE);

		return "features/profile/profile-preview";
	}

	@GetMapping(value = WebEndpoint.MY_PROFILE_UPDATE)
	public String getUpdateForm(Model model, HttpServletRequest request) {

		AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
		AppUser userMe = appUserService.findById(principleObject.getAppUserId());

		model.addAttribute("user", userMe);
		model.addAttribute("myProfilePath", WebEndpoint.MY_PROFILE);

		return "features/profile/profile-update";
	}

	@PostMapping(value = WebEndpoint.MY_PROFILE_UPDATE)
	public String submitUpdateForm(@ModelAttribute("user") AppUser updatedUser, HttpServletRequest request,
			Model model) {

		try {
			AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
			appUserService.updateOwnProfile(principleObject.getAppUserId(), updatedUser);

			Alert alert = Alert.builder().success(true).title("Success").details("Profile Updated").build();
			model.addAttribute("alert", alert);

			// For updating for the top right corner
			principleObject.setProfilePicturePath(updatedUser.getProfilePicturePath());

		} catch (Exception ex) {
			logger.error("Error During profile update", ex);
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}

		model.addAttribute("myProfilePath", WebEndpoint.MY_PROFILE);
		return "features/profile/profile-update";
	}
}
