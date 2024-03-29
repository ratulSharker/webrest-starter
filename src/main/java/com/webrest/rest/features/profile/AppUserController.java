package com.webrest.rest.features.profile;

import com.webrest.common.dto.response.Response;
import com.webrest.common.entity.AppUser;
import com.webrest.common.interceptor.AuthorizationInterceptor;
import com.webrest.common.service.AppUserService;
import com.webrest.rest.constants.RestRoutes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("AppUserControllerRest")
public class AppUserController {

	private AppUserService appUserService;

	public AppUserController(AppUserService appUserService) {
		this.appUserService = appUserService;
	}

	@GetMapping(value = RestRoutes.USERS_ME)
	public Response<AppUser> getMe(HttpServletRequest request) {
		AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
		AppUser userMe = appUserService.findById(principleObject.getAppUserId());
		return Response.<AppUser>builder().data(userMe).build();
	}

	@PutMapping(value = RestRoutes.USERS_ME)
	public Response<AppUser> updateMe(HttpServletRequest request, @Valid @RequestBody AppUser updatedAppUser) {
		AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
		AppUser updatedMe = appUserService.updateOwnProfile(principleObject.getAppUserId(), updatedAppUser);
		return Response.<AppUser>builder().data(updatedMe).build();
	}
}
