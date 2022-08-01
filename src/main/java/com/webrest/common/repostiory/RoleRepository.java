package com.webrest.common.repostiory;

import java.util.List;
import java.util.Optional;

import com.webrest.common.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
	
	@Query("SELECT COUNT(role) FROM Role role WHERE LOWER(role.name) = LOWER(:name)")
	public Long countByName(@Param("name") String name);

	@Query("SELECT COUNT(role) FROM Role role"
			+ " WHERE LOWER(role.name) = LOWER(:name) AND role.roleId != :roleId")
	public Long countByNameExceptRoleId(@Param("name") String name, @Param("roleId") Long roleId);

	@Query("SELECT role FROM Role role"
			+ " LEFT JOIN FETCH role.authorizations"
			+ " WHERE role.roleId = :roleId")
	public Optional<Role> getRoleDetailsById(@Param("roleId") Long roleId);

	@Query("SELECT role FROM Role role"
			+ " WHERE role.isSuperAdmin = false"
			+ " AND role.active = true")
	public List<Role> findNonSuperAdminActiveRoles();
}
