package kkukmoa.kkukmoa.store.domain;

import jakarta.persistence.*;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.store.enums.StoreRegistrationStatus;
import kkukmoa.kkukmoa.user.domain.User;
import lombok.*;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRegistration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신청자 (User 엔티티와 연관)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User applicant;

    // 매장 기본 정보
    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String storeAddress;

    private String storeAddressDetail;

    private Double latitude;
    private Double longitude;

    private String storePhoneNumber;

    private String storeImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalTime openingHours;

    private LocalTime closingHours;

    // 상태 (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    private StoreRegistrationStatus status;

    // 상태 변경
    public void changeStatus(StoreRegistrationStatus newStatus) {
        if (this.status == StoreRegistrationStatus.APPROVED) {
            throw new IllegalStateException("이미 승인된 신청입니다.");
        }
        this.status = newStatus;
    }
}
