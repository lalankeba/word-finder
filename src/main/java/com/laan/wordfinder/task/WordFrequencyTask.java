package com.laan.wordfinder.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WordFrequencyTask {

    @Cacheable(value = "wordFrequency", key = "{#k, #fileHash}")
    public Map<String, Integer> findFrequentWords(final File file, final Integer k, final String fileHash) throws IOException {
        log.info("Calculating frequent words from file, {}", fileHash);
        Map<String, Integer> map = new HashMap<>();

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
                line = line.replaceAll("\\?", " ");
                line = line.replaceAll("\\.$", " ");

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
}
