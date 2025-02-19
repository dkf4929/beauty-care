package com.project.beauty_care.global.health;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    // server health check
    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck(){}
}