package kkukmoa.kkukmoa.voucher.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.VoucherHandler;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.voucher.converter.VoucherConverter;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.dto.VoucherResponseDto;
import kkukmoa.kkukmoa.voucher.repository.VoucherRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherQueryService {

    private final VoucherRepository voucherRepository;
    private final AuthService authService;
    private final RedisTemplate<String, String> redisTemplate;

    // "voucher_"

    /** 내 금액권 목록 조회 */
    public List<VoucherResponseDto.VoucherListResponseDto> getMyVouchers() {
        User user = authService.getCurrentUser();

        return voucherRepository.findByUser(user).stream()
                .map(VoucherConverter::toListDto)
                .toList();
    }

    /** 금액권 상세 조회 */
    public VoucherResponseDto.VoucherDetailResponseDto getVoucherDetail(String qrCodeUuid) {
        Voucher voucher =
                voucherRepository
                        .findByQrCodeUuid(qrCodeUuid)
                        .orElseThrow(() -> new VoucherHandler(ErrorStatus.VOUCHER_NOT_FOUND));

        return VoucherConverter.toDetailDto(voucher);
    }
}
