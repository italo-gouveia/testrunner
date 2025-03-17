package com.mobiledev.testrunner.tests;

import com.mobiledev.testrunner.dto.TestRunRequest;
import com.mobiledev.testrunner.dto.TestRunResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestRunIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testSubmitTestRun() {
        TestRunRequest request = new TestRunRequest();
        request.setApkUrl("https://example.com/apk");
        request.setTestScript("https://example.com/test-script");
        request.setTimeout(50);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TestRunRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/test-runs", httpEntity, String.class);

        System.out.println("Response Body: " + response.getBody());
        System.out.println("Response Status: " + response.getStatusCode());

        // Assert the response status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}