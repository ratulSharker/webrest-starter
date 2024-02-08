package com.webrest.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webrest.common.annotation.ValidJson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }),
		@UniqueConstraint(columnNames = { "mobile" }) })
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long appUserId;

	@Column
	@NotEmpty(message = "`name` must not be empty")
	private String name;

	@Column
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@ValidJson(message = "`dob` must be given in number of millis since 1970")
	private Date dob;

	@Column
	@NotEmpty(message = "`email` must not be empty")
	private String email;

	@Column
	@NotEmpty(message = "`mobile` must not be empty")
	private String mobile;

	@Column
	@JsonIgnore
	private String password;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Column
	private String profilePicturePath;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "app_user_role", joinColumns = {@JoinColumn(name = "app_user_id")},
			inverseJoinColumns = {@JoinColumn(name = "role_id")})
	private Set<Role> roles = new HashSet<Role>();

	@PrePersist
	private void prePersist() {
		Date currentDate = new Date();
		this.createdAt = currentDate;
		this.updatedAt = currentDate;
	}

	@PreUpdate
	private void preUpdate() {
		this.updatedAt = new Date();
	}

	// TODO: Prepare a js which will ensure the serial
	// of the checked roles.
	public void setRoles(List<Long> roleIds) {
		roles.clear();
		if(CollectionUtils.isEmpty(roleIds) == false) {
			roleIds.stream().forEach(roleId -> {
				Role role = new Role();
				role.setRoleId(roleId);
				roles.add(role);
			});
		}
	}

	// TODO: Running complexity O(n), find a better way
	@JsonIgnore
	public boolean containsRoleId(Long roleId) {
		return roles.stream().filter(role -> {
			return Objects.equals(role.getRoleId(), roleId);
		}).findFirst().isPresent();
	}

	@JsonIgnore
	public List<Long> getRoleIds() {
		return roles.stream().map(Role::getRoleId).collect(Collectors.toList());
	}
}
