package com.laan.wordfinder.controller;

import com.laan.wordfinder.dto.WordFrequencyResponse;
import com.laan.wordfinder.service.WordFrequencyService;
import com.laan.wordfinder.util.PathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(PathUtil.UPLOAD)
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final WordFrequencyService wordFrequencyService;

    @PostMapping
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("k") Integer k) {
        log.info("Uploading file and extracting {} frequent word/s...", k);
        WordFrequencyResponse wordFrequencyResponse = wordFrequencyService.processFile(multipartFile, k);
        return new ResponseEntity<>(wordFrequencyResponse, HttpStatus.OK);
    }
}
