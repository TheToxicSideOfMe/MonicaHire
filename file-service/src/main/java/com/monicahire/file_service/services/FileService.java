package com.monicahire.file_service.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.monicahire.file_service.dtos.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final Cloudinary cloudinary;

    public UploadResponse upload(MultipartFile file, String folder) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto"  // handles PDFs, images, etc.
                )
        );

        String url = (String) result.get("secure_url");
        String publicId = (String) result.get("public_id");

        log.info("Uploaded file to Cloudinary: publicId={} url={}", publicId, url);
        return new UploadResponse(url, publicId);
    }

    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
        log.info("Deleted file from Cloudinary: publicId={}", publicId);
    }
}