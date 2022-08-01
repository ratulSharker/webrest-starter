package com.webrest.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import com.webrest.common.entity.Role;
import com.webrest.common.entity.RoleAuthorizationId;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.exception.BadRequestException;
import com.webrest.common.repostiory.RoleRepository;
import com.webrest.common.specification.RoleSpecification;
import org.springframework.beans.BeanUtils;
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

	@Transactional
	public Role createNewRole(Role role) {
		throwIfRoleFoundWithSameName(role.getName(), null);
		return roleRepository.save(role);
	}

	public Role getRoleDetails(Long roleId) {
		Optional<Role> optionalRole = roleRepository.getRoleDetailsById(roleId);
		return optionalRole.orElseThrow(() -> {
			throw new EntityNotFoundException("No role found with given id");
		});
	}

	public Set<Pair<AuthorizedFeature, AuthorizedAction>> getRoleFeatureActions(Role role) {
		return role.getAuthorizations().stream().map(authorization -> {
			RoleAuthorizationId roleAuthorizationId = authorization.getRoleAuthorizationId();
			return Pair.of(roleAuthorizationId.getFeature(), roleAuthorizationId.getAction());
		}).collect(Collectors.toSet());
	}

	@Transactional
	public Role updateRole(Role role) {
		throwIfRoleFoundWithSameName(role.getName(), role.getRoleId());
		Role existingRole = getRoleDetails(role.getRoleId());
		BeanUtils.copyProperties(role, existingRole, "roleId", "isSuperAdmin", "authorizations");

		existingRole.getAuthorizations().clear();
		existingRole.getAuthorizations().addAll(role.getAuthorizations());

		return roleRepository.save(role);
	}

	private void throwIfRoleFoundWithSameName(String roleName, Long exceptRoleId) {

		Long count = 0L;

		if(exceptRoleId != null) {
			count = roleRepository.countByNameExceptRoleId(roleName, exceptRoleId);
		} else {
			count = roleRepository.countByName(roleName);
		}

		if(count > 0) {
			throw new BadRequestException("Role exists with the given name");
		}
	}

	public List<Role> getNonSuperAdminActiveRoles() {
		return roleRepository.findNonSuperAdminActiveRoles();
	}
}
