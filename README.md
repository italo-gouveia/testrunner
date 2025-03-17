# TestRunner Application

## Description

TBD

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
- **Libraries:** Jackson (for JSON serialization).
- **Simulation:** Random delays and outcomes for test execution.