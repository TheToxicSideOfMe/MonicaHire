package com.monicahire.file_service.controllers;
import com.monicahire.file_service.dtos.UploadResponse;
import com.monicahire.file_service.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * Upload any file to Cloudinary.
     * folder param lets callers organize files e.g. "cvs", "reports"
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder
    ) {
        try {
            UploadResponse response = fileService.upload(file, folder);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    /**
     * Delete a file from Cloudinary by its public ID.
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("publicId") String publicId) {
        try {
            fileService.delete(publicId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed: " + e.getMessage());
        }
    }
}