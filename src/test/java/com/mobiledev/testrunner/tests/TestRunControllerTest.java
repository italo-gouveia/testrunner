package com.mobiledev.testrunner.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestRunControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSubmitTestRun() throws Exception {
        mockMvc.perform(post("/api/v1/test-runs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"apkUrl\": \"http://example.com/app.apk\", \"testScript\": \"script.sh\", \"timeout\": 10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runId").exists());
    }
}