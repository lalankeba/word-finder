package com.laan.wordfinder.controller;

import com.laan.wordfinder.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class MaxFileTest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    @Value("${app.admin.user.name}")
    private String adminUsername;

    @Value("${app.admin.user.password}")
    private String adminPassword;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost".concat(":" + port).concat(PathUtil.UPLOAD);
    }

    @Test
    void testMaxFileSize() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", getFileSystemResource("huge-file.txt"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        Assertions.assertThrows(ResourceAccessException.class,
                () -> testRestTemplate.withBasicAuth(adminUsername, adminPassword)
                .postForEntity(baseUrl + "?k=10", requestEntity, Object.class));

    }

    private FileSystemResource getFileSystemResource(final String fileName) {
        Path filePath = Paths.get("src", "test", "resources", "sample-files", fileName);
        return new FileSystemResource(filePath);
    }

}
