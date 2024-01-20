package com.laan.wordfinder.service.impl;

import com.laan.wordfinder.dto.WordFrequencyResponse;
import com.laan.wordfinder.exception.WordFrequencyException;
import com.laan.wordfinder.mapper.WordFrequencyMapper;
import com.laan.wordfinder.service.WordFrequencyService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordFrequencyServiceImpl implements WordFrequencyService {

    @Value("${app.file.path}")
    private String filePath;

    private final WordFrequencyMapper wordFrequencyMapper;

    @Override
    public WordFrequencyResponse processFile(final MultipartFile multipartFile, final Integer k) {
        try {
            Map<String, Integer> map = findFrequentWords(multipartFile, k);
            Long totalWords = countTotalWords(map);
            return wordFrequencyMapper.mapDetailsToResponse(map, totalWords);
        } catch (IOException e) {
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
        File currDir = new File(filePath);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + File.separator + UUID.randomUUID();

        File file = new File(fileLocation);
        multipartFile.transferTo(file);

        return file;
    }

    @PostConstruct
    private void init() {
        File fileDirectory = new File(filePath);
        if (!fileDirectory.exists()) {
            boolean folderCreated = fileDirectory.mkdirs();
            log.info("Folder {} created: {}", fileDirectory.getName(), folderCreated);
        }
    }
}
