package kkukmoa.kkukmoa.review.domain;

import jakarta.persistence.*;
import lombok.*;

// ReviewImage.java
// 설명: 리뷰에 종속되는 이미지. 정렬 보장을 위해 sortOrder 사용.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "review_images",
        indexes = {
                @Index(name = "idx_review_image_review", columnList = "review_id"),
                @Index(name = "idx_review_image_sort", columnList = "review_id, sort_order")
        })
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false, length = 2000) // S3 등 절대 URL
    private String imageUrl;

    @Column(name = "sort_order", nullable = false) // 0~4
    private int sortOrder;

    void setReview(Review review)    { this.review = review; }
    void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
