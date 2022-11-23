package com.webrest.common.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webrest.common.entity.Role;
import com.webrest.common.entity.RoleAuthorization;
import com.webrest.common.entity.RoleAuthorizationId;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.exception.BadRequestException;
import com.webrest.common.repostiory.RoleRepository;
import com.webrest.common.specification.RoleSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class RoleService {

	private final RoleRepository roleRepository;
	private final AuthorizationService authorizationService;
	private final RedisTemplate<String, String> stringRedisTemplate;
	private final ObjectMapper objectMapper;

	@Value("${ROLE_ACCESS_HASH_KEY}")
	private String ROLE_ACCESS_HASH_KEY;

	RoleService(RoleRepository roleRepository, @Lazy AuthorizationService authorizationService,
			RedisTemplate<String, String> stringRedisTemplate, ObjectMapper objectMapper) {
		this.roleRepository = roleRepository;
		this.authorizationService = authorizationService;
		this.stringRedisTemplate = stringRedisTemplate;
		this.objectMapper = objectMapper;
	}
	
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

		// Remove role feature action entry
		evictRoleFeatureActionKey(role.getRoleId());

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

	public List<Role> getActiveRoles() {
		return roleRepository.findActiveRoles();
	}

	public Role getOne(Long roleId) {
		return roleRepository.getOne(roleId);
	}

	public Map<AuthorizedFeature, Set<AuthorizedAction>> getAuthorizedFeatureActionsForGivenRoleIds(List<Long> roleIds) throws JsonProcessingException {

		List<String> roleIdStrings = roleIds.stream().map((roleId) -> roleId.toString()).collect(Collectors.toList());
		List<String> cachedFeatureActions = stringRedisTemplate.<String, String>opsForHash().multiGet(ROLE_ACCESS_HASH_KEY,
				roleIdStrings);

		Map<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> roleWiseCachedFeatureActions = getRoleWiseAuthorizedFeatureActionsFromCache(roleIds, cachedFeatureActions);
		List<Long> cacheMissedRoleIds = calculateMissedCacheRoleIds(roleIds, roleWiseCachedFeatureActions);

		// TODO: Use apache common utils
		if (CollectionUtils.isEmpty(cacheMissedRoleIds) == false) {
			Map<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> roleWiseFeatureActionsFromDB = getRoleWiseAuthorizedFeatureActionsFromDB(
					cacheMissedRoleIds);
			updateCache(roleWiseFeatureActionsFromDB);
			roleWiseCachedFeatureActions.putAll(roleWiseFeatureActionsFromDB);
		}

		// return roleRepository.rolesWithAuthorizationRoleIdIn(roleIds);
		return mergeFeatureActions(roleWiseCachedFeatureActions.values());
	}

	public Long getActiveRoleCount() {
		return roleRepository.countByActiveTrue();
	}

	private Map<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> getRoleWiseAuthorizedFeatureActionsFromCache(List<Long> roleIds,
			List<String> cachedFeatureActions) throws JsonMappingException, JsonProcessingException {
		Map<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> roleWiseCachedFeatureAction = new HashMap<>();
		for (int index = 0; index < cachedFeatureActions.size(); index++) {
			if (cachedFeatureActions.get(index) != null) {
				Long roleId = roleIds.get(index);
				String serializedFeatureAction = cachedFeatureActions.get(index);
				Map<AuthorizedFeature, Set<AuthorizedAction>> featureActions = objectMapper.readValue(
						serializedFeatureAction, new TypeReference<Map<AuthorizedFeature, Set<AuthorizedAction>>>() {

						});

				roleWiseCachedFeatureAction.put(roleId, featureActions);
			}
		}
		return roleWiseCachedFeatureAction;
	}

	private List<Long> calculateMissedCacheRoleIds(List<Long> roleIds,
			Map<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> roleWiseCachedFeatureActions) {
		return roleIds.stream().filter(roleId -> {
			return roleWiseCachedFeatureActions.containsKey(roleId) == false;
		}).collect(Collectors.toList());
	}

	private Map<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> getRoleWiseAuthorizedFeatureActionsFromDB(List<Long> roleIds) {
		List<Role> rolesWithAuthorization = roleRepository.rolesWithAuthorizationRoleIdIn(roleIds);

		Map<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> rolesAuthorizedFeature = new HashMap<>(); 
		for(Role role : rolesWithAuthorization) {
			Set<RoleAuthorization> authorizations = role.getAuthorizations();
			Map<AuthorizedFeature, Set<AuthorizedAction>> authorizedFeatureActions = new HashMap<>();
			for(RoleAuthorization authorization: authorizations) {
				AuthorizedFeature feature = authorization.getRoleAuthorizationId().getFeature();
				AuthorizedAction action = authorization.getRoleAuthorizationId().getAction();
				Set<AuthorizedAction> actions = authorizedFeatureActions.get(feature);
				if(actions == null) {
					actions = new HashSet<>();
					authorizedFeatureActions.put(feature, actions);
				}
				actions.add(action);
			}
			rolesAuthorizedFeature.put(role.getRoleId(), authorizedFeatureActions);
		}

		return rolesAuthorizedFeature;
	}

	private void updateCache(Map<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> rolesWithAuthorizedFeatureActions)
			throws JsonProcessingException {
		for (Entry<Long, Map<AuthorizedFeature, Set<AuthorizedAction>>> entry : rolesWithAuthorizedFeatureActions
				.entrySet()) {
			String roleId = entry.getKey().toString();
			String serializedFeatureActions = objectMapper.writeValueAsString(entry.getValue());
			stringRedisTemplate.<String, String>opsForHash().put(ROLE_ACCESS_HASH_KEY, roleId,
					serializedFeatureActions);
		}
	}

	private Map<AuthorizedFeature, Set<AuthorizedAction>> mergeFeatureActions(
			Collection<Map<AuthorizedFeature, Set<AuthorizedAction>>> featureActionsList) {
		Map<AuthorizedFeature, Set<AuthorizedAction>> mergedFeatureActions = new HashMap<>();
		for (Map<AuthorizedFeature, Set<AuthorizedAction>> featureActions : featureActionsList) {
			for (Entry<AuthorizedFeature, Set<AuthorizedAction>> entry : featureActions.entrySet()) {
				AuthorizedFeature incomingFeature = entry.getKey();
				Set<AuthorizedAction> incomingActions = entry.getValue();
				Set<AuthorizedAction> existingActions = mergedFeatureActions.get(incomingFeature);
				if (existingActions == null) {
					mergedFeatureActions.put(incomingFeature, incomingActions);
				} else {
					existingActions.addAll(incomingActions);
				}
			}
		}
		return mergedFeatureActions;
	}

	private void evictRoleFeatureActionKey(Long roleId) {
		stringRedisTemplate.<String, String>opsForHash().delete(ROLE_ACCESS_HASH_KEY, roleId.toString());
	}
}
