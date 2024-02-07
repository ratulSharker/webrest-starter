package com.webrest.common.entity;

import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}, name = "role_name_uk"))
public class Role {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roleId;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "Role name cannot be empty")
	private String name;

	@Column(nullable = false)
	private Boolean active;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "roleAuthorizationId.role", cascade = {
			CascadeType.ALL }, orphanRemoval = true)
	private Set<RoleAuthorization> authorizations;

	// Used for web form data binding
	public void setHyphenSeparatedFeatureActions(List<String> hyphenSeparatedFeatureActions) {
		authorizations = hyphenSeparatedFeatureActions.stream().map(hyphenSeparatedFeatureAction -> {
			String[] featureAction = hyphenSeparatedFeatureAction.split("-");
			AuthorizedFeature feature = AuthorizedFeature.valueOf(featureAction[0]);
			AuthorizedAction action = AuthorizedAction.valueOf(featureAction[1]);
			return Pair.of(feature, action);
		}).map(featureActionPair -> {
			return new RoleAuthorizationId(this, featureActionPair.getFirst(), featureActionPair.getSecond());
		}).map(roleAuthorizationId -> {
			return new RoleAuthorization(roleAuthorizationId);
		}).collect(Collectors.toSet());
	}
}
