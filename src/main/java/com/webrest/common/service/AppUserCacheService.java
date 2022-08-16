package com.webrest.common.service;

import java.util.Optional;

import com.webrest.common.config.RedisConfiguration;
import com.webrest.common.entity.AppUser;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AppUserCacheService {
	
	private final String USER_DOES_NOT_EXISTS_BY_ID_KEY = "'user-by-id-' + #userId + '-not-exists'";

	@Cacheable(value = RedisConfiguration.CACHE_CONFIGURATION_BOOLEAN, key = USER_DOES_NOT_EXISTS_BY_ID_KEY, unless = "#result == false")
	public boolean cacheConfirmsThatUserDoesNotExistsById(Long userId) {
		return false;
	}

	@Cacheable(value = RedisConfiguration.CACHE_CONFIGURATION_BOOLEAN, key = USER_DOES_NOT_EXISTS_BY_ID_KEY, unless = "#result == false")
	public boolean updateCacheConfirmationThatUserDoesNotExists(Long userId, Optional<AppUser> optionalAppUser) {
		return optionalAppUser.isEmpty();
	}

}
