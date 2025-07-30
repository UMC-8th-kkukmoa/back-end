package kkukmoa.kkukmoa.voucher.converter;

import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.DateUtil;
import kkukmoa.kkukmoa.common.util.QrCodeUtil;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.dto.VoucherResponseDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoucherConverter {

    private static final String PREFIX = "voucher_";

    public static VoucherResponseDto.VoucherListResponseDto toListDto(Voucher voucher) {
        return VoucherResponseDto.VoucherListResponseDto.builder()
                .name(voucher.getVoucherName())
                .qrCodeUuid(voucher.getQrCodeUuid())
                .validDays(voucher.getValidDays())
                .daysLeft(DateUtil.getDdayFromToday(voucher.getValidDays()))
                .status(voucher.getStatus().getDescription())
                .build();
    }

    public static VoucherResponseDto.VoucherDetailResponseDto toDetailDto(Voucher voucher) {
        return VoucherResponseDto.VoucherDetailResponseDto.builder()
                .qrCodeUuid(QrCodeUtil.removePrefix(voucher.getQrCodeUuid(), QrCodeType.VOUCHER))
                .name(voucher.getVoucherName())
                .value(voucher.getValue())
                .remainingValue(voucher.getRemainingValue())
                .validDays(voucher.getValidDays())
                .qrCode(QrCodeUtil.qrCodeToBase64(voucher.getQrCodeUuid()))
                .daysLeft(DateUtil.getDdayFromToday(voucher.getValidDays()))
                .status(voucher.getStatus().getDescription())
                .build();
    }

    public static VoucherResponseDto.VoucherDeductResponseDto toDeductDto(
            Voucher voucher, int useAmount) {
        return VoucherResponseDto.VoucherDeductResponseDto.builder()
                .name(voucher.getVoucherName())
                .usedAmount(useAmount)
                .validDays(voucher.getValidDays())
                .remainingValue(voucher.getRemainingValue())
                .build();
    }
}
