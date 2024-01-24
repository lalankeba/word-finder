package com.laan.wordfinder.validator;

import com.laan.wordfinder.exception.WordFinderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileValidator {

    private final Tika tika;

    public void validateFile(final MultipartFile multipartFile, Integer k) throws IOException {
        if (k < 1) {
            throw new WordFinderException("k: " + k + " must be greater than 1.");
        } else if (multipartFile.isEmpty()) {
            throw new WordFinderException("File: " + multipartFile.getOriginalFilename() + " is empty.");
        } else {
            String mediaType = tika.detect(multipartFile.getInputStream());
            if (!MediaType.TEXT_PLAIN_VALUE.equals(mediaType)) {
                throw new WordFinderException("Media type: " + mediaType + " of the uploaded file: " + multipartFile.getOriginalFilename() + " is invalid.");
            }
        }
    }

}
