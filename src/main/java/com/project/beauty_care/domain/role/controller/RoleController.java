package com.project.beauty_care.domain.role.controller;

import com.project.beauty_care.domain.role.dto.RoleCreateRequest;
import com.project.beauty_care.domain.role.dto.RoleMemberResponse;
import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.dto.RoleUpdateRequest;
import com.project.beauty_care.domain.role.service.RoleService;
import com.project.beauty_care.global.SuccessResponse;
import com.project.beauty_care.global.enums.SuccessCodes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ROLE REST API", description = "Role(API 권한) 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/role")
public class RoleController {
    private final RoleService service;

    @GetMapping
    public SuccessResponse<List<RoleMemberResponse>> findAllRoles(@RequestParam(name = "roleName", required = false) String roleName) {
        List<RoleMemberResponse> roles = service.findAllRoles(roleName);
        return SuccessResponse.success(SuccessCodes.RETRIEVE_SUCCESS, HttpStatus.OK, roles);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<RoleResponse> createRole(@RequestBody @Valid RoleCreateRequest request) {
        RoleResponse response = service.createRole(request);
        return SuccessResponse.success(SuccessCodes.SAVE_SUCCESS, HttpStatus.OK, response);
    }

    @PutMapping
    public SuccessResponse<RoleResponse> updateRole(@RequestBody @Valid RoleUpdateRequest request) {
        RoleResponse response = service.updateRole(request);
        return SuccessResponse.success(SuccessCodes.UPDATE_SUCCESS, HttpStatus.OK, response);
    }

    @DeleteMapping("/{role}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse hardDeleteRole(@PathVariable("role") String role) {
        service.hardDeleteRole(role);
        return SuccessResponse.success(SuccessCodes.DELETE_SUCCESS, HttpStatus.NO_CONTENT);
    }
}
