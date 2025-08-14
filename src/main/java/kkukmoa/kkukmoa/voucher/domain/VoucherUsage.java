package kkukmoa.kkukmoa.voucher.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherUsage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int usedAmount;

    private LocalDateTime usedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_id")
    private User user;

    public static VoucherUsage of(Voucher voucher, User user, int usedAmount) {
        VoucherUsage usage = new VoucherUsage();
        usage.voucher = voucher;
        usage.user = user;
        usage.usedAmount = usedAmount;
        usage.usedAt = LocalDateTime.now();
        return usage;
    }
}

