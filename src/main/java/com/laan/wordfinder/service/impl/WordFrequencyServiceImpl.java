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

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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
            String hash = getSha256Hash(multipartFile);

            Map<String, Integer> map = wordFrequencyTask.findFrequentWords(multipartFile, k, hash);

            return wordFrequencyMapper.mapDetailsToResponse(map, fileName);
        } catch (IOException e) {
            log.error("IOException occurred", e);
            throw new WordFinderException("Couldn't read the file. " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException occurred", e);
            throw new WordFinderException("Algorithm cannot be found. " + e.getMessage());
        }
    }

    private String getSha256Hash(final MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        byte[] data = multipartFile.getBytes();
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

}
