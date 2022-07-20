package com.webrest.common.specification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import com.webrest.common.entity.Role;
import com.webrest.common.entity.Role_;

public class RoleSpecification {
	
	public static Specification<Role> likeName(String searchString) {

		if (StringUtils.isBlank(searchString)) {
			return null;
		}

		return (root, query, builder) -> {
			String likeString = String.format("%%%s%%", searchString.toLowerCase());

			return builder.like(builder.lower(root.get(Role_.name)), likeString);
		};
	}

}
