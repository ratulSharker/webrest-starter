package com.webrest.common.repostiory;

import java.util.Optional;

import com.webrest.common.entity.AppUser;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends BaseRepository<AppUser, Long> {

	@Query(value = "SELECT au FROM AppUser au" + " LEFT JOIN FETCH au.roles "
			+ " WHERE (au.mobile = :mobileOrEmail OR au.email = :mobileOrEmail) AND"
			+ " au.password = :password")
	public Optional<AppUser> findByMobileOrEmailAndPassword(@Param("mobileOrEmail") String mobileOrEmail,
			@Param("password") String password);

	public Long countByMobile(String mobile);

	public Long countByEmail(String email);

	public Long countByMobileAndAppUserIdNot(String mobile, Long appUserId);

	public Long countByEmailAndAppUserIdNot(String mobile, Long appUserId);


	public Optional<AppUser> findByEmail(String email);

	@Modifying
	@Query("UPDATE AppUser user SET user.password = :password WHERE user.appUserId = :appUserId")
	public void updateUserPassword(@Param("appUserId") Long appUserId, @Param("password") String password);

	@Query("SELECT user FROM AppUser user"
			+ " LEFT JOIN FETCH user.roles"
			+ " WHERE user.appUserId = :appUserId")
	public Optional<AppUser> findByIdWithRoles(@Param("appUserId") Long appUserId);
}
