package kkukmoa.kkukmoa.voucher.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kkukmoa.kkukmoa.common.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VoucherUsageRow {
    private Long usageId;
    private Long voucherId;
    private Long storeId;
    private String storeName;
    private String storeImage;
    private Integer usedAmount;
    @JsonIgnore private LocalDateTime usedAt;

    public String getUsedAtFormatted() {
        return DateUtil.formatKoreanFullDateWithDay(this.usedAt);
    }
}
