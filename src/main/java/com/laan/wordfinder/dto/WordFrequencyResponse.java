package com.laan.wordfinder.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class WordFrequencyResponse {

    private Map<String, Integer> wordFrequencies;
    private String fileName;
}
