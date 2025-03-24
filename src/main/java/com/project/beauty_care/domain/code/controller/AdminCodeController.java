package com.project.beauty_care.domain.code.controller;

import com.project.beauty_care.domain.code.dto.AdminCodeResponse;
import com.project.beauty_care.domain.code.service.CodeService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "CODE REST API FOR ADMIN", description = "코드 API(ADMIN)")
@RequestMapping("/admin/code")
public class AdminCodeController {
    private final CodeService service;

    @GetMapping
    public SuccessResponse<AdminCodeResponse> findAllCode() {
        AdminCodeResponse code =  service.findAllCode();
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, HttpStatus.OK, code);
    }
}
