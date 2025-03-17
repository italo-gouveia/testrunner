# Test Runner Orchestrator (Java/Spring Boot)

## Description
A minimal test runner orchestration service that manages a pool of Android emulator workers.

## Implementation Plan
### Core Functionality:
#### 1. REST API:
- Implement endpoints for submitting test runs, checking status, and retrieving results.
- Include a /health endpoint (quietly, as per your instructions).

#### 2. Worker Management:
- Simulate a pool of emulator workers using an in-memory list.
- Assign test runs to available workers and handle worker failures or timeouts.

#### 3. Test Execution Simulation:
- Use random delays and outcomes to simulate test execution. 
- Store APK metadata and test results in memory.

### 4. Error Handling and Logging:
- Implement proper error handling for invalid inputs, worker failures, and timeouts.
- Add logging for key operations (e.g., test run submission, worker assignment).

### Tech Stack:
- **Language:** Java.
- **Framework:** Spring Boot.
- **Libraries:** Jackson (for JSON serialization), Lombok(for DTOs manipulations).
- **Simulation:** Random delays and outcomes for test execution.

## Running the Service
1. Build the project:
   ```bash
   ./mvnw clean package
   ```

2. Start the server:

    ```bash
    ./mvnw spring-boot:run
   ```
   
3. Access the API at http://localhost:8080.


### Submit a Test Run
- **POST /api/v1/test-runs**
   - Request Body:
     ```json
     {
       "apkUrl": "string",
       "testScript": "string",
       "timeout": "number"
     }
     ```
   - Response:
     ```json
     {
       "runId": "string"
     }
     ```

### Check Test Run Status
- **GET /api/v1/test-runs/{runId}**
   - Response:
     ```json
     {
       "status": "QUEUED|RUNNING|COMPLETED|FAILED",
       "worker": "string",
       "results": {...},
       "error": "string"
     }
     ```

## Design Decisions
- **Simplicity:** Used in-memory data structures to keep the implementation lightweight.
- **Concurrency:** Used a thread pool to handle multiple test runs concurrently.
- **Error Handling:** Added robust error handling for invalid inputs and worker failures.
