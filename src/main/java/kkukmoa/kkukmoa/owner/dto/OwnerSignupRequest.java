package kkukmoa.kkukmoa.owner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerSignupRequest {

    @NotBlank(message = "전화번호는 필수입니다.")
    @Schema(description = "사장님 전화번호", example = "01012345678")
    private String phoneNumber;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    @Schema(description = "비밀번호 (6자 이상)", example = "qwerty123")
    private String password;
}
