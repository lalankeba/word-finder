package com.laan.wordfinder.service;

import com.laan.wordfinder.dto.WordFrequencyResponse;
import org.springframework.web.multipart.MultipartFile;

public interface WordFrequencyService {

    WordFrequencyResponse processFile(MultipartFile multipartFile, Integer k);
}
