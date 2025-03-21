package com.project.beauty_care.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserMemberUpdateRequest {
    @NotNull(message = "사용자 ID를 입력하세요.")
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",
            message = "비밀번호는 8~16자의 영문 + 숫자 조합이어야 합니다"
    )
    @Schema(description = "비밀번호", example = "qwer1234")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",
            message = "비밀번호는 8~16자의 영문 + 숫자 조합이어야 합니다"
    )
    @Schema(description = "비밀번호 확인", example = "qwer1234")
    private String confirmPassword;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 20, message = "이름은 2~20자리의 문자 형태로 입력해야 합니다")
    @Schema(description = "사용자명", example = "user")
    private String name;

    @Builder
    public UserMemberUpdateRequest(Long id, String password, String confirmPassword, String name) {
        this.id = id;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
    }
}
