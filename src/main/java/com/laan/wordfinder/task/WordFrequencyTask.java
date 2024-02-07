package com.laan.wordfinder.task;

import com.laan.wordfinder.util.Trie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Component
@Slf4j
public class WordFrequencyTask {

    @Cacheable(value = "wordFrequency", key = "{#k, #fileHash}")
    public Map<String, Integer> findFrequentWords(final MultipartFile multipartFile, final Integer k, final String fileHash) throws IOException {
        log.info("Calculating frequent words from file, {}", fileHash);
        Trie trie = new Trie();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(multipartFile.getBytes())))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                line = line.replaceAll("\\. ", " ");
                line = line.replaceAll("; ", " ");
                line = line.replaceAll(": ", " ");
                line = line.replaceAll("\"", " ");
                line = line.replaceAll(", ", " ");
                line = line.replaceAll("\\(", " ");
                line = line.replaceAll("\\)", " ");
                line = line.replaceAll("\\?", " ");
                line = line.replaceAll("\\.$", " ");

                String[] words = line.split("\\s+");

                for (String word : words) {
                    if (!word.isEmpty()) {
                        trie.insert(word);
                    }
                }
            }
        }

        return trie.getTopFrequentWords(k);
    }
}
