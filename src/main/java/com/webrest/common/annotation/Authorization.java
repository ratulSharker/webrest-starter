package com.webrest.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;

import org.springframework.http.HttpMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Authorization {
	AuthorizedFeature feature() default AuthorizedFeature.NONE;
	AuthorizedAction action() default AuthorizedAction.NONE;
	boolean isPublicForAuthorizedUser() default false;
	boolean isPublic() default false;
	HttpMethod[] httpMethods() default {HttpMethod.GET}; // For web, this is pretty straight forward, 
	// but REST api's having some different stories.
}
