package com.webrest.common.dto.authentication.request.rest;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

	@NotEmpty(message = "`idToken` required")
	private String idToken;
}
