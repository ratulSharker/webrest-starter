package com.webrest.common.dto.authentication.request.web;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDto {

	@NotEmpty(message = "Mobile or email cannot be empty")
	private String mobileOrEmail;

	@NotEmpty(message = "Password cannot be empty")
	private String password;
}
