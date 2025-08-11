package kkukmoa.kkukmoa.review.repository;

import jakarta.persistence.EntityManager;
import kkukmoa.kkukmoa.review.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
class ReviewCursorRepositoryImpl implements ReviewCursorRepository {

    private final EntityManager em;

    @Override
    public List<Review> fetchNext(Long storeId, LocalDateTime createdAt, Long id, int size) {
        String jpql = """
            select r from Review r
            join fetch r.writer w
            left join fetch r.images imgs
            where r.store.id = :storeId
              and (r.createdAt < :createdAt
                   or (r.createdAt = :createdAt and r.id < :id))
            order by r.createdAt desc, r.id desc
            """;
        return em.createQuery(jpql, Review.class)
                .setParameter("storeId", storeId)
                .setParameter("createdAt", createdAt)
                .setParameter("id", id)
                .setMaxResults(size)
                .getResultList();
    }
}
