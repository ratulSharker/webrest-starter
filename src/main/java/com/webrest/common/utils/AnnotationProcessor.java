package com.webrest.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public class AnnotationProcessor {

	public static <T extends Annotation> Optional<T> readAnnotation(Class<?> targetClass, String fieldName,
			Class<T> annotationClass) {
		try {
			Field field = targetClass.getDeclaredField(fieldName);
			T annotation = field.getAnnotation(annotationClass);
			return Optional.of(annotation);
		} catch (Exception ex) {
			return Optional.empty();
		}
	}
}
