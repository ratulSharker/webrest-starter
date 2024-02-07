package com.webrest.common.dto.authentication.request.rest;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {

	@NotEmpty(message = "`name` required")
	private String name;

	@NotEmpty(message = "`email` required")
	private String email;

	@NotEmpty(message = "`idToken` required")
	private String idToken;
}
