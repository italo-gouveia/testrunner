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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Test Run API", description = "Endpoints for managing test runs")
public class TestRunController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestRunController.class);

    private final List<String> workers = new ArrayList<>(Arrays.asList("worker1", "worker2", "worker3"));
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
        executorService.submit(() -> executeTestRun(runId));
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
        if (workers.isEmpty()) {
            testRuns.put(runId, testRuns.get(runId).withStatus("FAILED").withError("No workers available"));
            return;
        }

        String worker = workers.remove(0);
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
                testRuns.put(runId, testRuns.get(runId).withStatus("FAILED").withError("Test failed"));
            }
        } catch (Exception e) {
            testRuns.put(runId, testRuns.get(runId).withStatus("FAILED").withError(e.getMessage()));
        } finally {
            workers.add(worker);
            logger.info("Test run completed: " + runId);
        }
    }

}