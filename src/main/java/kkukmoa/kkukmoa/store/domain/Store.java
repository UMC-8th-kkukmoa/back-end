package kkukmoa.kkukmoa.store.domain;

import jakarta.persistence.*;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.region.domain.Region;
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

    @Column(nullable = false, unique = true, length = 10)
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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;
}
