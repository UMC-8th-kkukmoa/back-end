package kkukmoa.kkukmoa.owner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto.QrScanDto;
import kkukmoa.kkukmoa.owner.service.OwnerCommandService;
import lombok.RequiredArgsConstructor;
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

  @PatchMapping("/qrcode") // TODO 쿠폰 전용 API -> 쿠폰 & 금액권 통합 API
  @Operation(summary = "(사장님) QR 코드 인식",
            description = "coupon_uuid 정보를 받아 금액 차감을 차감합니다. 금액 차감에 성공하면<br>"
                          + "- 쿠폰의 경우  <b>/v1/stamps/coupons</b> <br>"
                          + "- 금액권의 경우 <b>/v1/...</b> <br>"
                          + "으로 redirect 하도록 Web Socket 으로 메시지를 보냅니다.<br><br>"
                          + "웹소켓 요청 주소는 <b>ws://baseUrl/ws</b> 입니다.")
  public ApiResponse<OwnerQrResponseDto.QrScanDto> updateCouponUse(@RequestParam("qr") String qrCode) {
    QrScanDto qrScanDto = ownerCommandService.scanQrCode(qrCode);
    return ApiResponse.onSuccess(qrScanDto);
  }

}
