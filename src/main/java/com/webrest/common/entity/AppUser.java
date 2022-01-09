package com.webrest.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webrest.common.annotation.ValidJson;
import com.webrest.common.enums.AppUserType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
	@Enumerated(EnumType.STRING)
	private AppUserType appUserType;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Column
	private String profilePicturePath;

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
}
