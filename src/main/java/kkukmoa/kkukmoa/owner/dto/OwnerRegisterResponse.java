package kkukmoa.kkukmoa.owner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import kkukmoa.kkukmoa.store.enums.StoreStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OwnerRegisterResponse {

    @Schema(description = "매장 ID", example = "1")
    private Long storeId;

    @Schema(description = "신청 상태", example = "PENDING")
    private StoreStatus status;

    @Schema(description = "신청 일자", example = "2025-08-09T12:34:56")
    private LocalDateTime createdAt;
}
