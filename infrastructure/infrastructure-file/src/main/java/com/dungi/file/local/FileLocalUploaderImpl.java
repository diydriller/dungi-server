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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "file.kind", havingValue = "local")
public class FileLocalUploaderImpl implements FileUploader {
    @Value("${file.upload.path}")
    private String fileUploadPath;
    @Value("${file.down.path}")
    private String fileDownPath;

    public String imageUpload(MultipartFile file) throws IOException {
        String current_date = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        Path basePath = Paths.get(fileUploadPath);
        Files.createDirectories(basePath);

        String[] originFilenameParts = Optional.ofNullable(file.getOriginalFilename())
                .map(name -> name.split("\\."))
                .orElseThrow(() -> new IllegalArgumentException("파일 이름이 없습니다."));
        String originFileExt = originFilenameParts[originFilenameParts.length - 1];

        String imageName = "dungi-image" + current_date + "." + originFileExt;
        String imagePath = basePath.resolve(imageName).toString();
        String imageDownUrl = fileDownPath + imageName;

        File dest = new File(imagePath);
        file.transferTo(dest);

        return imageDownUrl;
    }
}
