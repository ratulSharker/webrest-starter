package com.webrest.common.repostiory;

import com.webrest.common.entity.Role;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaSpecificationExecutor<Role> {
	
}
