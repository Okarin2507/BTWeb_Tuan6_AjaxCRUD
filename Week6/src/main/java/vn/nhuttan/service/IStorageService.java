package vn.nhuttan.service;

import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
	void init();

	String getStorageFilename(MultipartFile file, String id);

	void store(MultipartFile file, String storedFilename);

	Resource loadAsResource(String filename);

	Path load(String filename);

	void delete(String storedFilename) throws Exception;
}