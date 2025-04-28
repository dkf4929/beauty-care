package com.project.beauty_care.global.scheduler;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SchedulerServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private SchedulerService service;

    @MockitoBean
    private MemberService memberService;

    @DisplayName("스케줄러 동작하는지 테스트")
    @Test
    void deleteMemberSchedule() {
        // 8초 wait => 호출 성공 여부
        Awaitility.await()
                .atMost(Duration.ofSeconds(8))
                .untilAsserted(() ->
                        verify(memberService, times(1))
                                .hardDeleteMemberWhenAfterOneYear(LocalDate.now())
                );
    }
}