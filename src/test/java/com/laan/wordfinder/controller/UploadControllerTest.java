package com.laan.wordfinder.controller;

import com.laan.wordfinder.util.PathUtil;
import com.laan.wordfinder.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @Test
    public void uploadFile() throws Exception {
        MockMultipartFile multipartFile = testUtils.getMultipartFile("file.txt");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD + "?k=" + 10).file(multipartFile)
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
        MockMultipartFile multipartFile = testUtils.getMultipartFile("file.txt");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD).file(multipartFile)
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
        MockMultipartFile multipartFile = testUtils.getMultipartFile("file.txt");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD + "?k=num").file(multipartFile)
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
        MockMultipartFile multipartFile = testUtils.getMultipartFile("pdf-file.pdf");

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart(PathUtil.UPLOAD + "?k=" + 50).file(multipartFile)
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

}
