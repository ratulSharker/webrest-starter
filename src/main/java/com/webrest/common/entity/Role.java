package com.webrest.common.entity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import org.springframework.data.util.Pair;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	// @Column(nullable = false)
	// private Boolean isSuperAdmin = false;

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
