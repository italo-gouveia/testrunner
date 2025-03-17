package com.mobiledev.testrunner;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TestRunController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestRunController.class);

    // Health endpoint
    @GetMapping("/health")
    public boolean health() {
        return true;
    }

}