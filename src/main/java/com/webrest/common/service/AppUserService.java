package com.webrest.common.service;

import com.webrest.common.entity.AppUser;
import com.webrest.common.entity.Role;
import com.webrest.common.exception.AppUserAlreadyExistsException;
import com.webrest.common.repostiory.AppUserRepository;
import com.webrest.common.service.storage.StorageService;
import com.webrest.common.service.storage.SubDirectory;
import com.webrest.common.specification.AppUserSpecification;
import com.webrest.common.utils.HashUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

	private final AppUserRepository appUserRepository;
	private final RoleService roleService;
	private final StorageService storageService;

	@Transactional(readOnly = true)
	public AppUser getAppUserMobileOrEmailAndPassword(String mobileOrEmail, String password) {
		Optional<AppUser> optionalAppUser = appUserRepository.findByMobileOrEmailAndPassword(mobileOrEmail, password);
		optionalAppUser.orElseThrow(() -> {
			String errMsg = String.format("User not found with emailOrEmail : %s", mobileOrEmail);
			log.info(errMsg);
			throw new EntityNotFoundException(errMsg);
		});

		return optionalAppUser.get();
	}

	@Transactional(readOnly = true)
	public AppUser getAppUserByEmail(String email) {
		Optional<AppUser> optionalUser = appUserRepository.findByEmail(email);
		return optionalUser.orElseThrow(() -> {
			String message = String.format("User not found with email : %s", email);
			throw new EntityNotFoundException(message);
		});
	}

	@Transactional(readOnly = true)
	public AppUser findById(Long userId) {
		Optional<AppUser> optionalAppUser = appUserRepository.findById(userId);

		return optionalAppUser.orElseThrow(() -> {
			String message = String.format("App user not found with id : %d", userId);
			throw new EntityNotFoundException(message);
		});
	}

	@Transactional(readOnly = true)
	public AppUser findByIdWithRoles(Long appUserId) {
		Optional<AppUser> optionalAppUser = appUserRepository.findByIdWithRoles(appUserId);

		return optionalAppUser.orElseThrow(() -> {
			String message = String.format("App user not found with id : %d", appUserId);
			throw new EntityNotFoundException(message);
		});
	}

	@Transactional
	public AppUser updateOwnProfile(Long appUserId, AppUser updatedUser) {
		AppUser existingUser = findById(appUserId);

		Long mobileCountExceptUser = appUserRepository.countByMobileAndAppUserIdNot(updatedUser.getMobile(), appUserId);
		if (mobileCountExceptUser > 0) {
			String message = String.format("Given mobile number `%s` is used by another user", updatedUser.getMobile());
			throw new RuntimeException(message);
		}

		Long emailCountExceptUser = appUserRepository.countByEmailAndAppUserIdNot(updatedUser.getEmail(), appUserId);
		if (emailCountExceptUser > 0) {
			String message = String.format("Given email address `%s` is used by another user", updatedUser.getEmail());
			throw new RuntimeException(message);
		}

		handleUpdateProfilePicture(updatedUser, existingUser);
		BeanUtils.copyProperties(updatedUser, existingUser, "appUserId", "password", "createdAt", "updatedAt");

		appUserRepository.save(existingUser);
		return existingUser;
	}

	@Transactional
	public AppUser update(Long appUserId, AppUser updatedUser) {
		AppUser existingUser = findById(appUserId);

		Long mobileCountExceptUser = appUserRepository.countByMobileAndAppUserIdNot(updatedUser.getMobile(), appUserId);
		if (mobileCountExceptUser > 0) {
			String message = String.format("Given mobile number `%s` is used by another user", updatedUser.getMobile());
			throw new RuntimeException(message);
		}

		Long emailCountExceptUser = appUserRepository.countByEmailAndAppUserIdNot(updatedUser.getEmail(), appUserId);
		if (emailCountExceptUser > 0) {
			String message = String.format("Given email address `%s` is used by another user", updatedUser.getEmail());
			throw new RuntimeException(message);
		}

		handleUpdateProfilePicture(updatedUser, existingUser);
		
		if(checkIfRawPasswordExistsThenHashIt(updatedUser)) {
			existingUser.setPassword(updatedUser.getPassword());
		}

		BeanUtils.copyProperties(updatedUser, existingUser, "appUserId", "password", "createdAt",
				"updatedAt", "roles");

		// Update roles
		existingUser.getRoles().clear();
		if(CollectionUtils.isEmpty(updatedUser.getRoles()) == false) {
			updatedUser.getRoles().stream().forEach(role -> {
				Role proxyRole = roleService.getOne(role.getRoleId());
				existingUser.getRoles().add(proxyRole);
			});
		}

		appUserRepository.save(existingUser);
		return existingUser;
	}

	private void handleUpdateProfilePicture(AppUser updatedUser, AppUser existingUser) {

		if (Objects.equals(updatedUser.getProfilePicturePath(), existingUser.getProfilePicturePath())) {
			// Both are same, so proceed
			// Nothing to do
			return;
		}

		if (StringUtils.isNotBlank(existingUser.getProfilePicturePath())) {
			// remove existing
			storageService.removePermanentFile(existingUser.getProfilePicturePath());
		}

		if (StringUtils.isNotBlank(updatedUser.getProfilePicturePath())) {
			// We are now sure, that it is the temp file
			try {
				storageService.moveTempFile(updatedUser.getProfilePicturePath(), SubDirectory.PROFILE_PICTURE);
				String finalPath = storageService.relativePath(SubDirectory.PROFILE_PICTURE,
						updatedUser.getProfilePicturePath());
				updatedUser.setProfilePicturePath(finalPath);
			} catch (Exception ex) {
				log.error("Error moving profile picture", ex);
				updatedUser.setProfilePicturePath(null);
			}
		}
	}

	public void detachAppUserFromJPA(AppUser appUser) {
		this.appUserRepository.detach(appUser);
	}

	@Transactional
	public void updatePassword(Long appUserId, String password) {
		appUserRepository.updateUserPassword(appUserId, password);
	}

	@Transactional(readOnly = true)
	public Page<AppUser> filter(Pageable pageable, String searchValue) {
		Specification<AppUser> specification = Specification
				.where(AppUserSpecification.likeNameOrMobileOrEmail(searchValue));

		return appUserRepository.findAll(specification, pageable);
	}

	@Transactional
	public AppUser createAppUser(AppUser appUser) {
		Long mobileCount = appUserRepository.countByMobile(appUser.getMobile());
		if (mobileCount > 0) {
			String errMsg = String.format("User exists with mobile : %s", appUser.getMobile());
			throw new AppUserAlreadyExistsException(errMsg);
		}

		Long emailCount = appUserRepository.countByEmail(appUser.getEmail());
		if (emailCount > 0) {
			String errMsg = String.format("User exists with email : %s", appUser.getMobile());
			throw new AppUserAlreadyExistsException(errMsg);
		}

		handleCreateProfilePicture(appUser);

		checkIfRawPasswordExistsThenHashIt(appUser);

		return appUserRepository.save(appUser);
	}

	private void handleCreateProfilePicture(AppUser newUser) {

		if (StringUtils.isNotBlank(newUser.getProfilePicturePath())) {
			// We are now sure, that it is the temp file
			try {
				storageService.moveTempFile(newUser.getProfilePicturePath(), SubDirectory.PROFILE_PICTURE);
				String finalPath = storageService.relativePath(SubDirectory.PROFILE_PICTURE,
						newUser.getProfilePicturePath());
				newUser.setProfilePicturePath(finalPath);
			} catch (Exception ex) {
				log.error("Error moving profile picture", ex);
				newUser.setProfilePicturePath(null);
			}
		}
	}

	@Transactional(readOnly = true)
	public Long getUserCount() {
		return appUserRepository.count();
	}

	private boolean checkIfRawPasswordExistsThenHashIt(AppUser appUser) {
		String plainTextPassword = appUser.getPassword(); 
		if(StringUtils.isNotBlank(plainTextPassword)) {
			String hashedPassword = HashUtils.hashPassword(plainTextPassword);
			appUser.setPassword(hashedPassword);
			return true;
		}
		return false;
	}

}
