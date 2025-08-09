package kkukmoa.kkukmoa.owner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerRegisterCheckResponse {

    @Schema(description = "PENDING 상태의 신청 존재 여부", example = "true")
    private boolean pending;

    @Schema(description = "상태 안내 메시지", example = "현재 신청이 검토 중입니다.")
    private String message;
}
