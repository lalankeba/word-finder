package com.laan.wordfinder.service.impl;

import com.laan.wordfinder.dto.WordFrequencyResponse;
import com.laan.wordfinder.exception.WordFrequencyException;
import com.laan.wordfinder.mapper.WordFrequencyMapper;
import com.laan.wordfinder.service.WordFrequencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordFrequencyServiceImpl implements WordFrequencyService {

    @Value("${uploaded.file.path}")
    private String uploadedFileLocation;

    private final WordFrequencyMapper wordFrequencyMapper;

    @Override
    public WordFrequencyResponse processFile(final MultipartFile multipartFile, final Integer k) {
        try {
            Map<String, Integer> map = findFrequentWords(multipartFile, k);
            Long totalWords = countTotalWords(map);
            return wordFrequencyMapper.mapDetailsToResponse(map, totalWords);
        } catch (IOException e) {
            log.error("IOException occurred", e);
            throw new WordFrequencyException("Couldn't read the file. " + e.getMessage());
        }
    }

    private Map<String, Integer> findFrequentWords(final MultipartFile multipartFile, final Integer k) throws IOException {
        Map<String, Integer> map = new HashMap<>();

        if (multipartFile.isEmpty()) {
            throw new WordFrequencyException("File must not be empty");
        }

        File file = saveFile(multipartFile);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                line = line.replaceAll("\\. ", " ");
                line = line.replaceAll("; ", " ");
                line = line.replaceAll(": ", " ");
                line = line.replaceAll("\"", " ");
                line = line.replaceAll(",", " ");
                line = line.replaceAll("\\(", " ");
                line = line.replaceAll("\\)", " ");


                String[] words = line.split("\\s+");

                for (String word : words) {
                    if (!word.isEmpty()) {
                        int count = Objects.nonNull(map.get(word)) ? map.get(word) : 0;
                        map.put(word, count + 1);
                    }
                }

            }

            map = map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(k)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }

        return map;
    }

    private Long countTotalWords(Map<String, Integer> map) {
        AtomicLong wordCount = new AtomicLong(0);
        map.values().forEach(wordCount::getAndAdd);
        return wordCount.get();
    }

    private File saveFile(final MultipartFile multipartFile) throws IOException {
        Path rootDir = Paths.get(uploadedFileLocation);
        if (!Files.exists(rootDir)) {
            rootDir = Files.createDirectory( Paths.get(uploadedFileLocation) );
        }
        Path destFile = rootDir.resolve(Paths.get(UUID.randomUUID().toString())).normalize().toAbsolutePath();

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, destFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return destFile.toFile();
    }

}
