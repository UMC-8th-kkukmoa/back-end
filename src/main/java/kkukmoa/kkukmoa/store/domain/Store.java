package kkukmoa.kkukmoa.store.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.store.enums.StoreStatus;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String number; // 전화번호

    @Column(nullable = true, unique = true, length = 10)
    private String merchantNumber; // 가맹점번호

    private String storeImage;

    private LocalTime openingHours;
    private LocalTime closingHours;

    private String qrUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StoreStatus status = StoreStatus.PENDING;

    // 신청 시 받는 상세 정보(주소/좌표)
    @Column(nullable = false)
    private String address;                  // 기본 주소
    private String addressDetail;            // 상세 주소
    private Double latitude;                 // 위도
    private Double longitude;                // 경도

    public void approve(String merchantNumber, User owner, Region region) {
        this.merchantNumber = merchantNumber;
        this.owner = owner;
        this.region = region;
    }

    public void changeStatus(StoreStatus next) {
        this.status = next;
    }

}
