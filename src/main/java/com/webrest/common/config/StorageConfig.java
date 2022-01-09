package com.webrest.common.config;

import com.webrest.common.service.storage.FileSystemStorageService;
import com.webrest.common.service.storage.StorageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

	@Bean
	public StorageService storageService(@Value("${FILE_STORAGE_TEMP_PATH}") String tempPath,
			@Value("${FILE_STORAGE_ROOT_PATH}") String storageRoot) {
		return new FileSystemStorageService(tempPath, storageRoot);
	}
}
