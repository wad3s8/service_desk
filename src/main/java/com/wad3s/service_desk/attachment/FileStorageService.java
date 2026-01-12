package com.wad3s.service_desk.attachment;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Path.of("uploads", "tickets");

    public String save(MultipartFile file, Long ticketId) {
        try {
            Files.createDirectories(root.resolve(String.valueOf(ticketId)));

            String safeName = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
            safeName = safeName.replaceAll("[\\\\/]+", "_");

            String key = ticketId + "/" + UUID.randomUUID() + "_" + safeName;
            Path target = root.resolve(key);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return key; // хранится в БД как storageKey
        } catch (IOException e) {
            throw new IllegalStateException("Cannot save file", e);
        }
    }

    public Resource load(String storageKey) {
        try {
            Path file = root.resolve(storageKey).normalize();

            if (!file.startsWith(root)) {
                throw new SecurityException("Invalid path");
            }

            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("File not found: " + storageKey);
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid file path: " + storageKey, e);
        }
    }
}

