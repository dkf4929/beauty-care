package com.project.beauty_care.global.scheduler;

import com.project.beauty_care.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SchedulerService {
    private final MemberService memberService;

    // 매일 03:00
    @Scheduled(cron = "${cron.member.delete}")
    public void deleteMemberSchedule() {
        memberService.hardDeleteMemberWhenAfterOneYear(LocalDate.now());
    }
}
