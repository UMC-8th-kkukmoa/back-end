package kkukmoa.kkukmoa.review.converter;

import kkukmoa.kkukmoa.review.domain.Review;
import kkukmoa.kkukmoa.review.domain.ReviewImage;
import kkukmoa.kkukmoa.review.dto.response.ReviewSummaryDto;

public class ReviewConverter {

    private ReviewSummaryDto toSummary(Review r) {
        return new ReviewSummaryDto(
                r.getId(),
                r.getWriter().getId(),
                r.getWriter().getNickname(),
                r.getContent(),
                r.getImages().stream().map(ReviewImage::getImageUrl).toList(),
                r.getCreatedAt());
    }
}
