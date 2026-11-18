package com.portfolio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/** Saves uploaded images to data/uploads/ and returns the URL path they're served at. */
@Service
public class ImageStorage {

	private static final Set<String> ALLOWED = Set.of("png", "jpg", "jpeg", "gif", "webp");

	private final Path uploadDir;

	public ImageStorage(@Value("${app.data-dir}") Path dataDir) throws IOException {
		this.uploadDir = dataDir.resolve("uploads");
		Files.createDirectories(uploadDir);
	}

	public Path getUploadDir() {
		return uploadDir;
	}

	/** Returns the new image's URL path, or null if no file was chosen. */
	public String save(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}
		String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
		ext = ext == null ? "" : ext.toLowerCase(Locale.ROOT);
		if (!ALLOWED.contains(ext)) {
			throw new IllegalArgumentException("Images must be PNG, JPG, GIF or WebP.");
		}
		String name = UUID.randomUUID() + "." + ext;
		try (InputStream in = file.getInputStream()) {
			Files.copy(in, uploadDir.resolve(name));
		}
		return "/uploads/" + name;
	}
}
