package com.project.beauty_care;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.beauty_care.config.TestSecurityConfig;
import com.project.beauty_care.domain.login.LoginController;
import com.project.beauty_care.domain.login.LoginService;
import com.project.beauty_care.global.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        LoginController.class
})
// 컨트롤러 -> security 비활성화
@SpringJUnitConfig(TestSecurityConfig.class)
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
}
