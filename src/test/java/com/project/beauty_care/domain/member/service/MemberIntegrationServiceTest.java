package com.project.beauty_care.domain.member.service;

import com.project.beauty_care.TestSupportWithOutRedis;
import com.project.beauty_care.domain.member.Member;
import com.project.beauty_care.domain.member.repository.MemberRepository;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.RequestInvalidException;
import com.project.beauty_care.global.security.dto.AppUser;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class MemberIntegrationServiceTest extends TestSupportWithOutRedis {
    @Autowired
    private MemberRepository repository;

    @Autowired
    private MemberService service;

    @MockitoBean
    private Logger logger;

    @DisplayName("회원 삭제 (hardDelete)")
    @Test
    @Transactional
    void hardDeleteMember() {
        // given
        LocalDate now = LocalDate.now();
        LocalDate expiredDate = now.minusYears(1);

        Member member1 = buildMember("test1", now, Boolean.FALSE);
        Member member2 = buildMember("test2", now, Boolean.FALSE);

        // 저장
        repository.saveAll(List.of(member1, member2));

        assertThat(repository.findAll())
                .hasSize(2)
                .extracting("deletedDate")
                .allMatch(d -> d.equals(now));

        // when
        // 삭제
        service.hardDeleteMemberWhenAfterOneYear(expiredDate);

        // then
        assertThat(repository.findAll())
                .isEmpty();
    }

    @DisplayName("회원 탈퇴를 취소 시나리오")
    @TestFactory
    @Transactional
    Collection<DynamicTest> deleteCancel() {
        final LocalDate now = LocalDate.now();

        return List.of(
                dynamicTest("탈퇴한 지 14일이 넘어가면, 취소 불가", () -> {
                    // given
                    repository.deleteAllInBatch();
                    final LocalDate expiredDate = now.minusDays(15);

                    Member member = buildMember("test1", expiredDate, Boolean.FALSE);
                    Member savedMember = repository.save(member);

                    assertThat(repository.findAll())
                            .hasSize(1)
                            .extracting("deletedDate")
                            .containsExactly(expiredDate);

                    AppUser user = buildAppUser(savedMember);

                    // when, then
                    assertThatThrownBy(() -> service.deleteCancel(user, now))
                            .isInstanceOf(RequestInvalidException.class)
                            .extracting("errors")
                            .satisfies(errors -> {
                                assertThat(errors).hasFieldOrPropertyWithValue("message",
                                        Errors.CAN_NOT_DELETE_CANCEL_AFTER_14_DAYS.getMessage());

                                assertThat(errors).hasFieldOrPropertyWithValue("errorCode",
                                        Errors.CAN_NOT_DELETE_CANCEL_AFTER_14_DAYS.getErrorCode());
                            });
                }),
                dynamicTest("정상 시나리오", () -> {
                    // given
                    repository.deleteAllInBatch();
                    final LocalDate expiredDate = now.minusDays(14);

                    Member member = buildMember("test1", expiredDate, Boolean.FALSE);
                    Member savedMember = repository.save(member);

                    assertThat(repository.findAll())
                            .hasSize(1)
                            .extracting("deletedDate", "isUse")
                            .containsExactly(Tuple.tuple(expiredDate, Boolean.FALSE));

                    AppUser user = buildAppUser(savedMember);

                    // when, then
                    Assertions.assertDoesNotThrow(() -> {
                        service.deleteCancel(user, now);

                        Member findMember = repository.findById(member.getId()).orElse(null);

                        assertThat(findMember)
                                .extracting("isUse", "deletedDate")
                                .containsExactly(Boolean.TRUE, null);
                    });
                })
        );
    }

    private static AppUser buildAppUser(Member savedMember) {
        return AppUser.builder()
                .memberId(savedMember.getId())
                .build();
    }

    private Member buildMember(String loginId, LocalDate deletedDate, Boolean isUse) {
        return Member.builder()
                .name("test")
                .loginId(loginId)
                .password("qwer1234")
                .isUse(isUse)
                .deletedDate(deletedDate)
                .build();
    }
}
