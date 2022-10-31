package com.webrest.common.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.client.util.Objects;
import com.webrest.common.annotation.ValidJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
			return Objects.equal(role.getRoleId(), roleId);
		}).findFirst().isPresent();
	}
}
