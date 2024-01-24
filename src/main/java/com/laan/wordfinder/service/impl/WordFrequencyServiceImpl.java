package com.laan.wordfinder.service.impl;

import com.laan.wordfinder.dto.WordFrequencyResponse;
import com.laan.wordfinder.exception.WordFinderException;
import com.laan.wordfinder.mapper.WordFrequencyMapper;
import com.laan.wordfinder.service.WordFrequencyService;
import com.laan.wordfinder.task.WordFrequencyTask;
import com.laan.wordfinder.validator.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordFrequencyServiceImpl implements WordFrequencyService {

    @Value("${uploaded.file.path}")
    private String uploadedFileLocation;

    private final WordFrequencyMapper wordFrequencyMapper;

    private final FileValidator fileValidator;

    private final WordFrequencyTask wordFrequencyTask;

    @Override
    public WordFrequencyResponse processFile(final MultipartFile multipartFile, final Integer k) {
        try {
            fileValidator.validateFile(multipartFile, k);
            String fileName = multipartFile.getOriginalFilename();
            log.info("Processing the file: {} for k: {} words", fileName, k);

            Path savedFilePath = saveFileInStorage(multipartFile);

            String hash = getSha256Hash(savedFilePath);

            Map<String, Integer> map = wordFrequencyTask.findFrequentWords(savedFilePath.toFile(), k, hash);

            deleteFileFromStorage(savedFilePath);

            return wordFrequencyMapper.mapDetailsToResponse(map, fileName);
        } catch (IOException e) {
            log.error("IOException occurred", e);
            throw new WordFinderException("Couldn't read the file. " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException occurred", e);
            throw new WordFinderException("Algorithm cannot be found. " + e.getMessage());
        }
    }

    private String getSha256Hash(final Path filePath) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(Paths.get(String.valueOf(filePath)));
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

    private Path saveFileInStorage(final MultipartFile multipartFile) throws IOException {
        log.info("Saving file temporarily in the storage");
        Path rootDir = Paths.get(uploadedFileLocation);
        if (!Files.exists(rootDir)) {
            rootDir = Files.createDirectory(Paths.get(uploadedFileLocation));
        }
        Path destFilePath = rootDir.resolve(Paths.get(UUID.randomUUID().toString())).normalize().toAbsolutePath();

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, destFilePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return destFilePath;
    }

    private void deleteFileFromStorage(final Path savedFilePath) throws IOException {
        log.info("Deleting file from the storage");
        if (Files.exists(savedFilePath)) {
            Files.delete(savedFilePath);
        }
    }

}
