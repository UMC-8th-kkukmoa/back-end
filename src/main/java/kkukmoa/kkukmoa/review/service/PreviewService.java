package kkukmoa.kkukmoa.review.service;

import kkukmoa.kkukmoa.review.dto.ReviewCardDto;
import kkukmoa.kkukmoa.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreviewService {

    // 리뷰 프리뷰

    private final ReviewRepository reviewRepository;

    public List<ReviewCardDto> getPreview(Long storeId, int limit) {
        return reviewRepository.findPreview(storeId, limit).stream()
                .map(r -> new ReviewCardDto(
                        r.getId(),
                        r.getWriter().getNickname(),
                        r.getImages().isEmpty() ? null : r.getImages().get(0).getImageUrl(),
                        snippet(r.getContent(), 10),
                        r.getCreatedAt()
                ))
                .toList();
    }

    private String snippet(String s, int len) {
        if (s == null) return null;
        return s.length() <= len ? s : s.substring(0, len) + "...";
    }

    public long countByStore(Long storeId) {
        return reviewRepository.countByStoreId(storeId);
    }
}
