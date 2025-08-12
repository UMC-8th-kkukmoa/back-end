package kkukmoa.kkukmoa.owner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerSignupRequest {

    // 로컬 로그인

    @NotBlank(message = "이메일 입력은 필수입니다.")
    @Schema(description = "사장님 이메일", example = "kkukadmin@naver.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    @Schema(description = "비밀번호 (6자 이상)", example = "qwerty123")
    private String password;
}
