package org.example.lablinkbackend.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage location: {}", this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String saveFile(MultipartFile file, String subDirectory, Long entityId) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path targetDir = fileStorageLocation.resolve(subDirectory);
            Files.createDirectories(targetDir);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = String.format("%d_%s%s", entityId, UUID.randomUUID().toString(), extension);
            Path targetLocation = targetDir.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation);

            String fileUrl = "/uploads/" + subDirectory + "/" + fileName;
            log.info("File saved: {}", fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("Could not save file", e);
            throw new RuntimeException("Could not save file: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String relativePath = fileUrl.replace("/uploads/", "");
            Path filePath = fileStorageLocation.resolve(relativePath);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            log.warn("Could not delete file: {}", e.getMessage());
        }
    }

    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }

        return file.getSize() <= 5 * 1024 * 1024;
    }

    public boolean isValidAttachment(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        return file.getSize() <= 10 * 1024 * 1024;
    }
}