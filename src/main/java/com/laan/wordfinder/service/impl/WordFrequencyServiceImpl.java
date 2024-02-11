package com.laan.wordfinder.service.impl;

import com.laan.wordfinder.dto.WordFrequencyResponse;
import com.laan.wordfinder.exception.WordFinderException;
import com.laan.wordfinder.mapper.WordFrequencyMapper;
import com.laan.wordfinder.service.WordFrequencyService;
import com.laan.wordfinder.task.WordFrequencyTask;
import com.laan.wordfinder.validator.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordFrequencyServiceImpl implements WordFrequencyService {

    private final WordFrequencyMapper wordFrequencyMapper;

    private final FileValidator fileValidator;

    private final WordFrequencyTask wordFrequencyTask;

    @Override
    public WordFrequencyResponse processFile(final MultipartFile multipartFile, final Integer k) {
        try {
            fileValidator.validateFile(multipartFile, k);
            String fileName = multipartFile.getOriginalFilename();
            log.info("Processing the file: {} for k: {} words", fileName, k);

            String text = extractText(multipartFile);
            String hash = getFileHash(multipartFile);
            Map<String, Integer> map = wordFrequencyTask.findFrequentWords(text, k, hash);

            return wordFrequencyMapper.mapDetailsToResponse(map, fileName);
        } catch (IOException e) {
            log.error("IOException occurred", e);
            throw new WordFinderException("Couldn't read the file. " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException occurred", e);
            throw new WordFinderException("Algorithm cannot be found. " + e.getMessage());
        }
    }

    private String extractText(final MultipartFile multipartFile) throws IOException {
        String text;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));

        try (bufferedReader) {
            text = bufferedReader.lines().collect(Collectors.joining("\n"));
        }
        return text;
    }

    private String getFileHash(final MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        byte[] data = multipartFile.getBytes();
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

}
