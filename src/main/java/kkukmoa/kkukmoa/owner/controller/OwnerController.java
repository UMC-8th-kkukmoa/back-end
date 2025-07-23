package kkukmoa.kkukmoa.owner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto.QrScanDto;
import kkukmoa.kkukmoa.owner.service.OwnerCommandService;
import kkukmoa.kkukmoa.owner.service.OwnerQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사장님 API", description = "사장님 권한이 필요합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owners")
public class OwnerController {

    private final OwnerCommandService ownerCommandService;
    private final OwnerQueryService ownerQueryService;

    @PatchMapping("/qrcode")
    @Operation(
            summary = "( 사장님 ) 고객 QR 코드 인식 후 처리 API",
            description =
                    "QR 코드 정보를 받아 차감할 금액을 반환합니다. 금액 차감에 성공하면<br>"
                            + "- 쿠폰의 경우  <b>/v1/stamps/coupons</b> <br>"
                            + "- 금액권의 경우 <b>/v1/...</b> <br>"
                            + "으로 redirect 하도록 Web Socket 으로 메시지를 보냅니다.<br><br>"
                            + "웹소켓 요청 주소는 <b>ws://baseUrl/ws</b> 입니다.")
    public ApiResponse<OwnerQrResponseDto.QrScanDto> updateCouponUse(
            @RequestParam("qr") String qrCode) {
        QrScanDto qrScanDto = ownerCommandService.scanQrCode(qrCode);
        return ApiResponse.onSuccess(qrScanDto);
    }

    @GetMapping("/qrcode/stamps")
    @Operation(
            summary = "( 사장님 ) 스탬프 적립 QR 코드 발급·조회 API",
            description =
                    "고객에게 보여줄 스탬프 적립용 QR 코드를 발급 받는 API 입니다.<br>생성한 QR 코드의 유효 시간은 1분입니다. 1분이 지나면 해당"
                        + " API를 다시 호출하여 새로운 QR 코드를 발급 받으세요.")
    public ApiResponse<OwnerQrResponseDto.QrDto> getStampQrCode() {
        OwnerQrResponseDto.QrDto stamp = ownerQueryService.getStamp();
        return ApiResponse.onSuccess(stamp);
    }
}
