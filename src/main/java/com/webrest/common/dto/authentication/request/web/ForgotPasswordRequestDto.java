package com.webrest.common.dto.authentication.request.web;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequestDto {

	@NotEmpty(message = "User email cannot be empty")
	private String email;
}
