package kkukmoa.kkukmoa.voucher.converter;

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
                .validDays(voucher.getValidDays())
                .status(voucher.getStatus())
                .build();
    }

    public static VoucherResponseDto.VoucherDetailResponseDto toDetailDto(Voucher voucher) {
        return VoucherResponseDto.VoucherDetailResponseDto.builder()
                .qrCodeUuid(removePrefix(voucher.getQrCodeUuid()))
                .name(voucher.getVoucherName())
                .value(voucher.getValue())
                .validDays(voucher.getValidDays())
                .qrCode(voucher.getQrImage())
                .status(voucher.getStatus())
                .build();
    }

    private static String removePrefix(String uuid) {
        if (uuid != null && uuid.startsWith(PREFIX)) {
            return uuid.substring(PREFIX.length());
        }
        return uuid;
    }
}