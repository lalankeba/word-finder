package com.laan.wordfinder.task;

import com.laan.wordfinder.util.Trie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class WordFrequencyTask {

    @Cacheable(value = "wordFrequency", key = "{#k, #fileHash}")
    public Map<String, Integer> findFrequentWords(final String text, final Integer k, final String fileHash) {
        log.info("Calculating frequent words from text, {}", fileHash);
        Trie trie = new Trie();

        String modText = text.replaceAll("\\p{Punct}", " ");
        String[] words = modText.toLowerCase().split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                trie.insert(word);
            }
        }

        return trie.getTopFrequentWords(k);
    }
}
