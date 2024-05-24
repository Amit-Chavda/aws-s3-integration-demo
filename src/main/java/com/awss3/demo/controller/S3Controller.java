package com.awss3.demo.controller;

import com.awss3.demo.dto.GenericResponse;
import com.awss3.demo.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<GenericResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        s3Service.uploadFile(file.getOriginalFilename(), file.getBytes());
        return ResponseEntity.ok(GenericResponse.ok("File uploaded successfully"));
    }

    @GetMapping
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) {
        return s3Service.downloadFile(fileName);
    }

    @GetMapping("/all")
    public ResponseEntity<GenericResponse> listFiles() {
        return ResponseEntity.ok(GenericResponse.ok("Files listed successfully", s3Service.listFiles()));
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse> deleteFile(@RequestParam String fileName) {
        return ResponseEntity.ok(GenericResponse.ok(s3Service.deleteFile(fileName) ? "File deleted successfully" : "File not found!"));
    }
}