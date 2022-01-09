package com.webrest.common.service.storage;

import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	public void saveAsTemp(MultipartFile file, String filename);

	public default String uniqueFilename(MultipartFile file) {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		return String.format("%d.%s", System.currentTimeMillis(), extension);
	}

	public void moveTempFile(String tempFileName, SubDirectory subDirectory);

	public void clearTemp();

	public boolean removePermanentFile(String filename);

	public InputStream getPermanentFileInputStream(String filename);

	public Long getPermanentFileSize(String filename);

	public String getPermanentFileMimeType(String filename);

	public String relativePath(SubDirectory subDirectory, String path);

	public default String moveFromTempToSubDirectory(String tempFileName, SubDirectory subDirectory) {
		if (StringUtils.isNotBlank(tempFileName)) {
			this.moveTempFile(tempFileName, subDirectory);
			String finalPath = this.relativePath(subDirectory, tempFileName);
			return finalPath;
		} else {
			return null;
		}
	}

	public default String handleMoveFromTempToSubDirectoryWithExistingFile(String updatedFilePath,
			String existingFilePath, SubDirectory subDirectory) {

		if (Objects.equals(updatedFilePath, existingFilePath)) {
			// Both are same, so proceed
			// Nothing to do
			return existingFilePath;
		}

		if (StringUtils.isNotBlank(existingFilePath)) {
			// remove existing
			this.removePermanentFile(existingFilePath);
		}

		// Now try to move the updated file from temp to subDirectory
		return moveFromTempToSubDirectory(updatedFilePath, subDirectory);
	}
}
