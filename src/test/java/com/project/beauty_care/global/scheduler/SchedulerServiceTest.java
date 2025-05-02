package com.project.beauty_care.global.scheduler;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.member.service.MemberService;
import com.project.beauty_care.global.scheduler.service.SchedulerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SchedulerServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private SchedulerService service;

    @MockitoBean
    private MemberService memberService;

    @DisplayName("스케줄러 동작하는지 테스트")
    @CsvSource({"3, 8"})
    @ParameterizedTest
    void deleteMemberSchedule(Long duration) {
        // 호출 성공 여부
        Awaitility.await()
                .atMost(Duration.ofSeconds(duration))
                .untilAsserted(() -> {
                    if (duration < 5)
                        verify(memberService, times(0)).hardDeleteMemberWhenAfterOneYear(LocalDate.now());
                    else
                        verify(memberService, times(1)).hardDeleteMemberWhenAfterOneYear(LocalDate.now());
                });
    }
}