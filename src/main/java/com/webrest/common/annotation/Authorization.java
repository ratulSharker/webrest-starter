package com.webrest.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Authorization {
	AuthorizedFeature feature = null;
	AuthorizedAction action = null;
	boolean isPublicForAuthorizedUser = false;
	boolean isPublic = false;	
}
