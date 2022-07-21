package com.webrest.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.webrest.common.entity.Role;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.repostiory.RoleRepository;
import com.webrest.common.specification.RoleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;
	private final AuthorizationService authorizationService;
	
	public Page<Role> filter(Pageable pageable, String searchValue) {
		Specification<Role> specification = RoleSpecification.likeName(searchValue);
		return roleRepository.findAll(specification, pageable);
	}

	public List<Pair<AuthorizedFeature, List<AuthorizedAction>>> getAssignableFeaturesWithAction() {
		Map<AuthorizedFeature, Set<AuthorizedAction>> featuresWithAction = authorizationService
				.getFeaturesWithActions();

		return featuresWithAction.entrySet().stream().filter(entrySet -> {
			return AuthorizedFeature.NONE.equals(entrySet.getKey()) == false;
		}).map(entrySet -> {
			List<AuthorizedAction> actions = new ArrayList<AuthorizedAction>(entrySet.getValue());
			return Pair.of(entrySet.getKey(), actions);
		}).collect(Collectors.toList());
	}
}
