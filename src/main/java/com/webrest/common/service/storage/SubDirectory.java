package com.webrest.common.service.storage;

import lombok.Getter;

@Getter
public enum SubDirectory {
	PROFILE_PICTURE("profile-picture"), SOCIETY_LOGO("society-logo"), SOCIETY_DISPLAY_IMAGE("society-display-image"),
	SOCIETY_MESSAGE_OWNER_PICTURE("society-message-owner"), PROGRAM_THEME_IMAGE("program-theme-image"),
	PROGRAM_MESSAGE_OWNER_PICTURE("program-message-owner"), FACULTY_IMAGE("faculty-image");

	private String directoryName;

	SubDirectory(String directoryName) {
		this.directoryName = directoryName;
	}
}
