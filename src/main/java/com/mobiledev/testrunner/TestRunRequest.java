package com.mobiledev.testrunner;

import lombok.Data;

@Data
public class TestRunRequest {
    private String apkUrl;
    private String testScript;
    private int timeout;
}