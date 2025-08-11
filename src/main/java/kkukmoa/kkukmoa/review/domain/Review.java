package kkukmoa.kkukmoa.review.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "reviews",
        indexes = {
            @Index(name = "idx_review_store", columnList = "store_id"),
            @Index(name = "idx_review_user", columnList = "user_id"),
            @Index(name = "idx_review_created_at", columnList = "created_at")
        })
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // 작성자
    @JoinColumn(name = "user_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // 대상 가게
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(length = 1000) // 본문(선택), 길이 제한
    private String content;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC") // 업로드 순서 보장
    @Builder.Default
    private List<ReviewImage> images = new ArrayList<>();

    /** 생성 시에만 이미지 추가 허용(최대 5장) */
    public void addImageOnCreate(ReviewImage image) {
        if (images.size() >= 5) throw new IllegalStateException("이미지는 최대 5장까지 가능합니다.");
        image.setReview(this);
        image.setSortOrder(images.size()); // 0부터 순차 부여
        images.add(image);
    }
}
