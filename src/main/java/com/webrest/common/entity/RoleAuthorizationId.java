package com.webrest.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleAuthorizationId implements Serializable {
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AuthorizedFeature feature;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AuthorizedAction action;
}
