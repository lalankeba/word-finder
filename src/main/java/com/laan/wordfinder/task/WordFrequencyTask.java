package com.laan.wordfinder.task;

import com.laan.wordfinder.util.Trie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class WordFrequencyTask {

    @Cacheable(value = "wordFrequency", key = "{#k, #fileHash}")
    public Map<String, Integer> findFrequentWords(final File file, final Integer k, final String fileHash) throws IOException {
        log.info("Calculating frequent words from file, {}", fileHash);
        Trie trie = new Trie();

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
                        trie.insert(word);
                    }
                }
            }
        }

        return trie.getTopFrequentWords(k);
    }
}
