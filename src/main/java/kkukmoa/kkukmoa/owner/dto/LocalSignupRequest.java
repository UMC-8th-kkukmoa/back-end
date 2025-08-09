package kkukmoa.kkukmoa.owner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocalSignupRequest {

    @NotBlank(message = "전화번호는 필수입니다.")
    @Schema(description = "사장님 전화번호", example = "01012345678")
    private String phoneNumber;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    // 필수 약관 동의
    @AssertTrue(message = "서비스 이용약관에 동의해야 합니다.")
    private boolean agreeTerms;

    @AssertTrue(message = "개인정보 처리방침에 동의해야 합니다.")
    private boolean agreePrivacy;
}
