package kkukmoa.kkukmoa.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class LocalSignupRequestDto {

    @NotBlank(message = "닉네임 입력은 필수입니다.")
    @Schema(description = "한글, 영문, 숫자만 입력 가능", example = "해리포터")
    private String nickname;

    @NotBlank(message = "생년월일 입력은 필수입니다.")
    @Schema(description = "생년월일 8자리", example = "2001.11.20")
    private LocalDate birthday;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "이메일", example = "demo@kkukmoa.shop")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    @Schema(description = "비밀번호(6자 이상)", example = "passw0rd")
    private String password;

    // 필수 약관 동의
    @AssertTrue(message = "서비스 이용약관에 동의해야 합니다.")
    private boolean agreeTerms;

    @AssertTrue(message = "개인정보 처리방침에 동의해야 합니다.")
    private boolean agreePrivacy;
}
