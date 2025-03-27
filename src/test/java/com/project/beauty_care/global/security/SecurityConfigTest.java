package com.project.beauty_care.global.security;

import com.project.beauty_care.IntegrationTestSupport;
import com.project.beauty_care.domain.member.dto.MemberResponse;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.domain.role.Role;
import com.project.beauty_care.domain.role.service.RoleService;
import com.project.beauty_care.global.enums.Authentication;
import com.project.beauty_care.global.enums.ErrorCodes;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest extends IntegrationTestSupport {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    final String RETRIEVE_SUCCESS_MESSAGE = "조회가 완료 되었습니다.";

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RoleService roleService;

    @DisplayName("권한에 따른 멤버 조회 API 호출 시나리오")
    @TestFactory
    Collection<DynamicTest> createMemberWithAuthentication() throws Exception {
        return List.of(
                DynamicTest.dynamicTest("어드민 권한 => API 호출 성공", () -> {
                    // given
                    final String API_PATH = "/admin/member";

                    when(roleService.findRolesByUrlPattern(API_PATH))
                            .thenReturn(List.of(buildRole(Authentication.ADMIN.getName())));

                    when(memberService.findAllMembers())
                            .thenReturn(buildAllMembers());

                    when(jwtTokenProvider.resolveToken(any()))
                            .thenReturn("Bearer token");

                    when(jwtTokenProvider.validateToken(anyString()))
                            .thenReturn(Boolean.TRUE);

                    when(jwtTokenProvider.getAuthentication(anyString()))
                            .thenReturn(
                                    buildAuthentication("admin", "admin", buildRole(Authentication.ADMIN.getName()))
                            );

                    // when, then
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get(API_PATH)
                            )
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.successCode").value("200"))
                            .andExpect(jsonPath("$.successMessage").value(RETRIEVE_SUCCESS_MESSAGE))
                            .andExpect(jsonPath("$.data").isArray())
                            .andExpect(jsonPath("$.data.length()").value(2))
                            .andExpect(jsonPath("$.data[0].id").value(1))
                            .andExpect(jsonPath("$.data[1].id").value(2))
                            .andExpect(jsonPath("$.data[0].loginId").value("admin"))
                            .andExpect(jsonPath("$.data[1].loginId").value("user"));

                    verify(memberService, times(1)).findAllMembers();
                }),
                DynamicTest.dynamicTest("사용자 권한 API 호출 시도 => FORBIDDEN", () -> {
                    when(jwtTokenProvider.resolveToken(any()))
                            .thenReturn("Bearer token");

                    when(jwtTokenProvider.validateToken(anyString()))
                            .thenReturn(Boolean.TRUE);

                    when(jwtTokenProvider.getAuthentication(anyString()))
                            .thenReturn(
                                    buildAuthentication("user", "user", buildRole(Authentication.USER.getName()))
                            );

                    // when, then
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/admin/member")
                            )
                            .andDo(print())
                            .andExpect(status().isForbidden())
                            .andExpect(jsonPath("$.code").value(ErrorCodes.FORBIDDEN.getErrorCode()))
                            .andExpect(jsonPath("$.message").value(Errors.AUTHORITY_NOT.getMessage()));
                }),
                DynamicTest.dynamicTest("로그인 하지 않고, API 호출 시도 => UNAUTHORIZED", () -> {
                    when(jwtTokenProvider.resolveToken(any())).thenReturn(null);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/admin/member")
                            )
                            .andDo(print())
                            .andExpect(status().isUnauthorized())
                            .andExpect(jsonPath("$.code").value(ErrorCodes.UNAUTHORIZED.getErrorCode()))
                            .andExpect(jsonPath("$.message").value(Errors.NOT_LOGIN_USER.getMessage()));
                })
        );
    }


    private List<MemberResponse> buildAllMembers() {
        return List.of(
                MemberResponse.builder()
                        .isUse(Boolean.TRUE)
                        .id(1L)
                        .name("admin")
                        .role(Authentication.ADMIN.getName())
                        .loginId("admin")
                        .build(),
                MemberResponse.builder()
                        .isUse(Boolean.TRUE)
                        .id(2L)
                        .name("user")
                        .role(Authentication.USER.getName())
                        .loginId("user")
                        .build()
        );
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(String name, String loginId, Role role) {
        return new UsernamePasswordAuthenticationToken(
                AppUser.builder()
                        .memberId(1L)
                        .name(name)
                        .loginId(loginId)
                        .role(role)
                        .build(),
                "1234",
                List.of(new SimpleGrantedAuthority(role.getRoleName()))
        );
    }

    private Role buildRole(String role) {
        return Role.builder()
                .roleName(role)
                .urlPatterns(Collections.emptyMap())
                .build();
    }
}
