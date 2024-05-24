package com.awss3.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.awss3.demo.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AppProperties appProperties;
    private final AmazonS3 amazonS3;

    public void uploadFile(String key, byte[] data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);

        amazonS3.putObject(appProperties.awsS3BucketName(), key, new ByteArrayInputStream(data), metadata);
    }

    public ResponseEntity<Resource> downloadFile(String fileName) {

        S3Object s3object = amazonS3.getObject(new GetObjectRequest(appProperties.awsS3BucketName(), fileName));

        Resource resource = new InputStreamResource(s3object.getObjectContent());

        String headerValue = "inline; filename=\"" + resource.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(getContentType(fileName))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }


    public List<String> listFiles() {

        ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName(appProperties.awsS3BucketName());

        ObjectListing objectListing = amazonS3.listObjects(request);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();

        return Stream.of(objectSummaries.listIterator())
                .map(s3ObjectListIterator -> objectSummaries.stream().map(S3ObjectSummary::getKey).toList())
                .flatMap(List::stream)
                .toList();
    }

    public boolean deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(appProperties.awsS3BucketName(), fileName));
            log.info("File '{}' deleted successfully from bucket '{}'", fileName, appProperties.awsS3BucketName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return fileExists(fileName);
    }

    public boolean fileExists(String fileName) {
        return listFiles().contains(fileName);
    }

    public MediaType getContentType(String fileName) {

        if (fileName.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        } else if (fileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (fileName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (fileName.contains(".txt")) {
            return MediaType.TEXT_PLAIN;
        } else if (fileName.contains(".csv")) {
            return MediaType.parseMediaType("text/csv");
        }
        return MediaType.APPLICATION_JSON;
    }
}