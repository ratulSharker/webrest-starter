package com.webrest.common.service;

import com.webrest.common.entity.Role;
import com.webrest.common.repostiory.RoleRepository;
import com.webrest.common.specification.RoleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;
	
	public Page<Role> filter(Pageable pageable, String searchValue) {
		Specification<Role> specification = RoleSpecification.likeName(searchValue);
		return roleRepository.findAll(specification, pageable);
	}

}
