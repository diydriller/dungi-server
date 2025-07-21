package com.dungi.file.local;

import com.dungi.core.integration.file.FileUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@ConditionalOnProperty(name = "file.kind", havingValue = "local")
public class FileLocalUploaderImpl implements FileUploader {
    @Value("${file.upload.path}")
    private String fileUploadPath;
    @Value("${file.down.path}")
    private String fileDownPath;

    public String imageUpload(MultipartFile file) throws IOException {
        String current_date = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File folder = new File(fileUploadPath);
        folder.mkdirs();
        String basePath = folder.getAbsolutePath();

        String[] imageFileFlags = file.getOriginalFilename().split("\\.");
        String imageExt = imageFileFlags[imageFileFlags.length - 1];

        String imagePath = basePath + "/image" + current_date + "." + imageExt;
        String imageDownUrl = fileDownPath + "image" + current_date + "." + imageExt;

        File dest = new File(imagePath);
        file.transferTo(dest);

        return imageDownUrl;
    }
}
