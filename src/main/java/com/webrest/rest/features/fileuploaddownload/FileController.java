package com.webrest.rest.features.fileuploaddownload;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import com.webrest.common.dto.file.FileUploadResponseDto;
import com.webrest.common.dto.response.Response;
import com.webrest.common.service.storage.StorageService;
import com.webrest.rest.constants.RestRoutes;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

	public StorageService storageService;

	public FileController(StorageService storageService) {
		this.storageService = storageService;
	}

	@PostMapping(value = RestRoutes.FILE_UPLOAD)
	public Response<FileUploadResponseDto> upload(@RequestParam("file") MultipartFile file) {

		String tempFilename = this.storageService.uniqueFilename(file);
		this.storageService.saveAsTemp(file, tempFilename);

		FileUploadResponseDto dto = FileUploadResponseDto.builder().tempFilename(tempFilename).build();
		return Response.<FileUploadResponseDto>builder().data(dto).build();
	}

	@GetMapping(value = RestRoutes.FILE_DOWNLOAD)
	public ResponseEntity<Resource> download(HttpServletRequest request) {

		String basePath = request.getContextPath() + RestRoutes.FILE_DOWNLOAD.replace("**", "");
		String subpath = request.getRequestURI().replace(basePath, "");

		InputStream inputStream = storageService.getPermanentFileInputStream(subpath);
		Long fileSize = storageService.getPermanentFileSize(subpath);
		String mimeType = storageService.getPermanentFileMimeType(subpath);
		Resource resource = new InputStreamResource(inputStream);

		return ResponseEntity.ok().contentLength(fileSize).header("Content-Type", mimeType)
				.cacheControl(CacheControl.maxAge(10000, TimeUnit.DAYS).cachePrivate()).body(resource);
	}
}
