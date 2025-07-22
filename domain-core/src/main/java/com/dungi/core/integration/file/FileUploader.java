package com.dungi.core.integration.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileUploader {
    String imageUpload(MultipartFile file) throws Exception;
}