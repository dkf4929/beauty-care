package com.project.beauty_care.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreateRequest {
    @NotBlank(message = "로그인 ID는 필수입니다")
    @Size(min = 4, max = 10, message = "아이디는 4~10자리의 문자 형태로 입력해야 합니다")
    @Schema(description = "로그인 아이디", example = "user")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",
            message = "비밀번호는 8~16자의 영문 + 숫자 조합이어야 합니다"
    )
    @Schema(description = "패스워드", example = "qwer1234")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 20, message = "이름은 2~20자리의 문자 형태로 입력해야 합니다")
    @Schema(description = "사용자명", example = "user")
    private String name;
}
