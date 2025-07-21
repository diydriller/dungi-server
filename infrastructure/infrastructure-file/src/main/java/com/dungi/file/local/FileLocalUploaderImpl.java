package com.dungi.file.local;

import com.dungi.core.integration.file.FileUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.dungi.common.util.FileUtil.extractFileExt;
import static com.dungi.common.util.StringUtil.FILE_PREFIX;

@Component
@ConditionalOnProperty(name = "file.kind", havingValue = "local")
public class FileLocalUploaderImpl implements FileUploader {
    @Value("${file.upload.path}")
    private String fileUploadPath;
    @Value("${file.down.path}")
    private String fileDownPath;

    public String imageUpload(MultipartFile file) throws IOException {
        Path basePath = Paths.get(fileUploadPath);
        Files.createDirectories(basePath);

        String originFileExt = extractFileExt(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String imageName = FILE_PREFIX + uuid + "." + originFileExt;
        String imagePath = basePath.resolve(imageName).toString();
        String imageDownUrl = fileDownPath + imageName;

        File dest = new File(imagePath);
        file.transferTo(dest);

        return imageDownUrl;
    }
}
