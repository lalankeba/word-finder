package com.laan.wordfinder.mapper;

import com.laan.wordfinder.dto.WordFrequencyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface WordFrequencyMapper {

    @Mapping(target = "wordFrequencies", source = "map")
    @Mapping(target = "fileName", source = "name")
    WordFrequencyResponse mapDetailsToResponse(Map<String, Integer> map, String name);

}
