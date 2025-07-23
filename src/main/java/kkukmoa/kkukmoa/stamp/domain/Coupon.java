package kkukmoa.kkukmoa.stamp.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private Integer discountAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CouponStatus status = CouponStatus.UNUSED;

    //  @Lob
    //  @Column(columnDefinition = "MEDIUMBLOB")
    //  private byte[] qrImage;

    @Column(nullable = false, length = 50)
    private String qrCode;

    // 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user; // Coupon -> User 단방향 매핑

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id")
    private Store store; // Coupon -> Store 단방향 매핑

    public void use() {
        this.status = CouponStatus.USED;
    }
}
