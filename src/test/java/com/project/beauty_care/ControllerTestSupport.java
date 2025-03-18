package com.project.beauty_care;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.beauty_care.domain.member.controller.AdminMemberController;
import com.project.beauty_care.domain.member.controller.PublicMemberController;
import com.project.beauty_care.domain.member.controller.UserMemberController;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.login.controller.LoginController;
import com.project.beauty_care.global.login.service.LoginService;
import com.project.beauty_care.global.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        LoginController.class,
        PublicMemberController.class,
        UserMemberController.class,
        AdminMemberController.class
})
@AutoConfigureMockMvc(addFilters = false)
public class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected LoginService loginService;

    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    protected MemberService memberService;
}
