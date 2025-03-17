package com.mobiledev.testrunner;

import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Getter
public class TestRunStatus {
    // QUEUED, RUNNING, COMPLETED, FAILED
    private String status;
    private String apkUrl;
    private String testScript;
    private int timeout;
    private String worker;
    private Map<String, Object> results;
    private String error;

    public TestRunStatus(String status, String apkUrl, String testScript, int timeout) {
        this.status = status;
        this.apkUrl = apkUrl;
        this.testScript = testScript;
        this.timeout = timeout;
    }

    // Fluent setters for immutability
    public TestRunStatus withStatus(String status) {
        this.status = status;
        return this;
    }
    public TestRunStatus withWorker(String worker) {
        this.worker = worker;
        return this;
    }
    public TestRunStatus withResults(Map<String, Object> results) {
        this.results = results;
        return this;
    }
    public TestRunStatus withError(String error) {
        this.error = error;
        return this;
    }
}