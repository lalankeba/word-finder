package com.laan.wordfinder.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestUtils {

    public MockMultipartFile getMultipartFile(final String fileName) throws IOException {
        Path path = Paths.get("src", "test", "resources", "sample-files", fileName).toAbsolutePath();
        InputStream inputStream = new FileInputStream(path.toFile());
        return new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, inputStream);
    }
}
