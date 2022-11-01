package com.webrest.common.specification;

import javax.persistence.criteria.Predicate;

import com.webrest.common.entity.AppUser;
import com.webrest.common.entity.AppUser_;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class AppUserSpecification {

	public static Specification<AppUser> likeNameOrMobileOrEmail(String searchString) {

		if (StringUtils.isBlank(searchString)) {
			return null;
		}

		return (root, query, builder) -> {
			String likeString = String.format("%%%s%%", searchString.toLowerCase());
			Predicate likeName = builder.like(builder.lower(root.get(AppUser_.name)), likeString);
			Predicate likeMobile = builder.like(builder.lower(root.get(AppUser_.mobile)), likeString);
			Predicate likeEmail = builder.like(builder.lower(root.get(AppUser_.email)), likeString);

			return builder.or(likeName, likeMobile, likeEmail);
		};
	}

}
