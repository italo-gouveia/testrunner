package com.mobiledev.testrunner.controller;

import com.mobiledev.testrunner.dto.TestRunRequest;
import com.mobiledev.testrunner.dto.TestRunResponse;
import com.mobiledev.testrunner.dto.TestRunStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Test Run API", description = "Endpoints for managing test runs")
public class TestRunController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestRunController.class);

    private final Queue<String> workers = new ConcurrentLinkedQueue<>(Arrays.asList("worker1", "worker2", "worker3"));
    private final Map<String, TestRunStatus> testRuns = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // Health endpoint
    @Operation(summary = "Check service health", description = "Check if the service is running.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    @GetMapping("/health")
    public boolean health() {
        return true;
    }

    // Submit a test run
    @Operation(summary = "Submit a test run", description = "Submit a new test run with an APK and test script.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test run submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/test-runs")
    public TestRunResponse submitTestRun(@RequestBody TestRunRequest request) {
        String runId = UUID.randomUUID().toString();
        testRuns.put(runId, new TestRunStatus(
                "QUEUED",
                request.getApkUrl(),
                request.getTestScript(),
                request.getTimeout()
        ));
        logger.info("Test run submitted: " + runId);
        // Submit the test run with retry logic
        executorService.submit(() -> retryTestRun(runId, 3)); // Retry up to 3 times
        return new TestRunResponse(runId);
    }

    // Check test run status
    @Operation(summary = "Get test run status", description = "Retrieve the status of a test run by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test run status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Test run not found")
    })
    @GetMapping("/test-runs/{runId}")
    public TestRunStatus getTestRunStatus(@PathVariable String runId) {
        TestRunStatus status = testRuns.get(runId);
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Test run not found");
        }
        return status;
    }

    // Simulate test execution
    private void executeTestRun(String runId) {
        // Poll a worker from the queue (thread-safe operation)
        String worker = workers.poll();

        if (worker == null) {
            // No workers available
            String errorMessage = "No workers available";
            logger.error("Test run {} failed: {}", runId, errorMessage);
            testRuns.put(runId, testRuns.get(runId).withStatus("FAILED").withError(errorMessage));
            return;
        }

        // Update test run status to RUNNING and assign the worker
        testRuns.put(runId, testRuns.get(runId).withStatus("RUNNING").withWorker(worker));

        try {
            // Simulate test execution with random delay
            int delay = new Random().nextInt(10) + 1;
            Thread.sleep(delay * 1000L);

            // Simulate pass/fail outcome
            if (new Random().nextBoolean()) {
                Map<String, Object> results = new HashMap<>();
                results.put("passed", true);
                results.put("logs", "Test passed");
                testRuns.put(runId, testRuns.get(runId).withStatus("COMPLETED").withResults(results));
            } else {
                String errorMessage = "Test failed";
                logger.error("Test run {} failed: {}", runId, errorMessage);
                testRuns.put(runId, testRuns.get(runId).withStatus("FAILED").withError(errorMessage));
            }
        } catch (Exception e) {
            // Log the exception with stack trace
            logger.error("Test run {} failed with exception: {}", runId, e.getMessage(), e);
            testRuns.put(runId, testRuns.get(runId).withStatus("FAILED").withError(e.getMessage()));
        } finally {
            // Return the worker to the pool
            workers.add(worker);
            logger.info("Test run {} completed with status: {}", runId, testRuns.get(runId).getStatus());
        }
    }

    private void retryTestRun(String runId, int retryCount) {
        if (retryCount <= 0) {
            logger.error("Test run {} failed after maximum retries", runId);
            testRuns.put(runId, testRuns.get(runId).withStatus("FAILED").withError("Maximum retries exceeded"));
            return;
        }

        logger.info("Retrying test run {} (attempts left: {})", runId, retryCount);
        executeTestRun(runId);

        // Check if the test run failed again
        TestRunStatus status = testRuns.get(runId);
        if ("FAILED".equals(status.getStatus())) {
            retryTestRun(runId, retryCount - 1);
        }
    }
}