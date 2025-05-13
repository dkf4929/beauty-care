package com.project.beauty_care.global.scheduler.controller;

import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import com.project.beauty_care.global.scheduler.service.SchedulerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 스케줄러 직접 실행
@Tag(name = "SCHEDULER REST API FOR ADMIN", description = "스케줄러 API")
@RequestMapping("/admin/scheduler")
@RequiredArgsConstructor
@RestController
public class SchedulerController {
    private final SchedulerService service;

    @PostMapping("/temp-file")
    public SuccessResponse deleteTempFileScheduler() {
        service.deleteTempFileSchedule();
        return SuccessResponse.success(SuccessCodes.DELETE_SUCCESS);
    }
}
