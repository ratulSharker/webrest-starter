package com.webrest.common.dto.authentication.request.web;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoverPasswordRequestDto {

	@NotEmpty(message = "Token cannot be empty")
	private String token;

	@NotEmpty(message = "Password cannot be empty")
	private String password;
}
