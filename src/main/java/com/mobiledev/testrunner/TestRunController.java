package com.mobiledev.testrunner;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
public class TestRunController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestRunController.class);

    private final List<String> workers = new ArrayList<>(Arrays.asList("worker1", "worker2", "worker3"));
    private final Map<String, TestRunStatus> testRuns = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // Health endpoint
    @GetMapping("/health")
    public boolean health() {
        return true;
    }

    // Submit a test run
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
        //TODO: Implement the executeTestRun method
        //executorService.submit(() -> executeTestRun(runId));
        return new TestRunResponse(runId);
    }

    // Check test run status
    @GetMapping("/test-runs/{runId}")
    public TestRunStatus getTestRunStatus(@PathVariable String runId) {
        TestRunStatus status = testRuns.get(runId);
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Test run not found");
        }
        return status;
    }


}