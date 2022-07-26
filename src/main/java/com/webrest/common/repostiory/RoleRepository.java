package com.webrest.common.repostiory;

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
}
