package com.webrest.common.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RoleAuthorization {

	@EmbeddedId
	private RoleAuthorizationId roleAuthorizationId;
}
