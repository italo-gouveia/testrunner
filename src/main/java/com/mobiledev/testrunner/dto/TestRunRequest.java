package com.mobiledev.testrunner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestRunRequest {
    @NotBlank(message = "apkUrl is required")
    private String apkUrl;

    @NotBlank(message = "testScript is required")
    private String testScript;

    @NotNull(message = "timeout is required")
    private int timeout;
}