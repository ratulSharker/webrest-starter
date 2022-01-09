package com.webrest.common.service.storage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.webrest.common.exception.FileNotFoundException;
import com.webrest.common.exception.FileUploadOrMoveException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileSystemStorageService implements StorageService {

	private Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);

	private final Path tempRootPath;
	private final Path storageRootPath;

	public FileSystemStorageService(String tempPath, String storageRoot) {
		this.tempRootPath = Paths.get(tempPath);
		this.storageRootPath = Paths.get(storageRoot);
	}

	@Override
	public void saveAsTemp(MultipartFile file, String filename) {
		try {
			createDirectories(this.tempRootPath);
			Files.copy(file.getInputStream(), this.tempRootPath.resolve(filename));
		} catch (Exception ex) {
			logger.error("File save as temp error", ex);
			throw new FileUploadOrMoveException("Error during file upload");
		}
	}

	@Override
	public void moveTempFile(String tempFileName, SubDirectory subDirectory) {
		try {
			createDirectories(storageRootPath.resolve(subDirectory.getDirectoryName()));
			Files.move(tempRootPath.resolve(tempFileName),
					storageRootPath.resolve(subDirectory.getDirectoryName()).resolve(tempFileName));
		} catch (Exception ex) {
			logger.error("File move from temp error", ex);
			throw new FileUploadOrMoveException("Error during file upload");
		}
	}

	@Override
	public void clearTemp() {
		throw new FileUploadOrMoveException("Not implemented yet");
	}

	@Override
	public boolean removePermanentFile(String filename) {
		try {
			return Files.deleteIfExists(storageRootPath.resolve(filename));
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public InputStream getPermanentFileInputStream(String filename) {
		try {
			Path path = storageRootPath.resolve(filename);
			return Files.newInputStream(path);
		} catch (Exception ex) {
			// Consume the exception here
			throw new FileNotFoundException("File not found");
		}
	}

	@Override
	public Long getPermanentFileSize(String filename) {
		try {
			Path path = storageRootPath.resolve(filename);
			return Files.size(path);
		} catch (Exception ex) {
			// Consume the exception here
			throw new FileNotFoundException("File not found");
		}
	}

	@Override
	public String getPermanentFileMimeType(String filename) {
		try {
			Path path = storageRootPath.resolve(filename);
			return Files.probeContentType(path);
		} catch (Exception ex) {
			// Consume the exception here
			throw new FileNotFoundException("File not found");
		}
	}

	@Override
	public String relativePath(SubDirectory subDirectory, String path) {
		return Paths.get(subDirectory.getDirectoryName(), path).toString();
	}

	private void createDirectories(Path path) {
		try {
			if (Files.notExists(path)) {
				Files.createDirectories(path);
			}
		} catch (Exception ex) {
			// just consume the error
		}
	}

}
