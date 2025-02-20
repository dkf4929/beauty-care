package com.project.beauty_care.global.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    // server health check
    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "헬스 체크",
            description = "서버 상태 health 여부 확인")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "healthy"
            )
    })
    public void healthCheck(){}
}