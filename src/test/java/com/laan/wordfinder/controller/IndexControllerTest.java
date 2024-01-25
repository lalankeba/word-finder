package com.laan.wordfinder.controller;

import com.laan.wordfinder.util.PathUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void init() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get(PathUtil.WELCOME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(
                        document("{method-name}",
                                preprocessResponse(prettyPrint())
                        ));
    }

    @Test
    void testUnsupportedMethod() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(PathUtil.WELCOME))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }
}
