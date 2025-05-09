package com.project.beauty_care.global.scheduler.service;

import com.project.beauty_care.domain.attachFile.service.AttachFileService;
import com.project.beauty_care.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SchedulerService {
    private final MemberService memberService;
    private final AttachFileService attachFileService;

    // 매일 03:00
    @Scheduled(cron = "${cron.member.delete}")
    public void deleteMemberSchedule() {
        memberService.hardDeleteMemberWhenAfterOneYear(LocalDate.now());
    }

    // 하루에 한번 임시 파일을 삭제한다.
    @Scheduled(cron = "${cron.temp-file.delete}")
    public void deleteTempFileSchedule() {
        attachFileService.deleteTempFileSchedule(LocalDateTime.now());
    }
}
