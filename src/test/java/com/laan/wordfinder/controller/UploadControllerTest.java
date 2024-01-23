package com.laan.wordfinder.controller;

import com.laan.wordfinder.util.PathUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class UploadControllerTest {

    @Value("${app.admin.user.name}")
    private String adminUsername;

    @Value("${app.admin.user.password}")
    private String adminPassword;

    @Value("${app.basic.user.name}")
    private String basicUsername;

    @Value("${app.basic.user.password}")
    private String basicPassword;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void uploadFile() throws Exception {
        MockMultipartFile multipartFile = getMultipartFile("file.txt");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD + "?k=" + 10).file(multipartFile)
                                .with(httpBasic(adminUsername, adminPassword))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordFrequencies").exists())
                .andDo(
                        document("{method-name}",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(parameterWithName("k").description("Required number of most frequent words")),
                                responseFields(
                                        subsectionWithPath("wordFrequencies").description("The k most frequent words and their frequency"))
                                        .and(fieldWithPath("fileName").description("Name of the uploaded file"))
                        ));
    }

    @Test
    public void uploadFileWithoutK() throws Exception {
        MockMultipartFile multipartFile = getMultipartFile("file.txt");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD).file(multipartFile)
                                .with(httpBasic(adminUsername, adminPassword))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andDo(
                        document("{method-name}",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("type").description("Type of the problem"))
                                        .and(fieldWithPath("title").description("Title of the error"))
                                        .and(fieldWithPath("status").description("Status code"))
                                        .and(fieldWithPath("detail").description("Details of the error"))
                                        .and(fieldWithPath("instance").description("Instance of the error"))
                        ));
    }

    @Test
    public void uploadFileWithInvalidK() throws Exception {
        MockMultipartFile multipartFile = getMultipartFile("file.txt");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD + "?k=num").file(multipartFile)
                                .with(httpBasic(adminUsername, adminPassword))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andDo(
                        document("{method-name}",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("type").description("Type of the problem"))
                                        .and(fieldWithPath("title").description("Title of the error"))
                                        .and(fieldWithPath("status").description("Status code"))
                                        .and(fieldWithPath("detail").description("Details of the error"))
                                        .and(fieldWithPath("instance").description("Instance of the error"))
                        ));
    }

    @Test
    public void uploadFileWithDifferentMediaType() throws Exception {
        MockMultipartFile multipartFile = getMultipartFile("pdf-file.pdf");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD + "?k=" + 50).file(multipartFile)
                                .with(httpBasic(adminUsername, adminPassword))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andDo(
                        document("{method-name}",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("type").description("Type of the problem"))
                                        .and(fieldWithPath("title").description("Title of the error"))
                                        .and(fieldWithPath("status").description("Status code"))
                                        .and(fieldWithPath("detail").description("Details of the error"))
                                        .and(fieldWithPath("instance").description("Instance of the error"))
                        ));
    }

    @Test
    public void unauthorizedAccess() throws Exception {
        MockMultipartFile multipartFile = getMultipartFile("file.txt");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD + "?k=" + 10).file(multipartFile)
                                .with(httpBasic(basicUsername, basicPassword))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").exists())
                .andDo(
                        document("{method-name}",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        ));
    }

    private MockMultipartFile getMultipartFile(final String fileName) throws IOException {
        Path path = Paths.get("src", "test", "resources", "sample-files", fileName).toAbsolutePath();
        InputStream inputStream = new FileInputStream(path.toFile());
        return new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, inputStream);
    }

}
