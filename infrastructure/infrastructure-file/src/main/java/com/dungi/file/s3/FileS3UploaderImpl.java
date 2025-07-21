package com.dungi.file.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dungi.core.integration.file.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.dungi.common.util.FileUtil.extractFileExt;

@Component
@ConditionalOnProperty(name = "file.kind", havingValue = "s3")
@RequiredArgsConstructor
public class FileS3UploaderImpl implements FileUploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String imageUpload(MultipartFile file) throws Exception {
        String originFileExt = extractFileExt(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String originalFilename = "dungi-image" + uuid + "." + originFileExt;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3Client.putObject(bucket, originalFilename, file.getInputStream(), metadata);
        return amazonS3Client.getUrl(bucket, originalFilename).toString();
    }
}
