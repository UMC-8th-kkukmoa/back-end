package kkukmoa.kkukmoa.store.repository;

import kkukmoa.kkukmoa.store.domain.StoreRegistration;
import kkukmoa.kkukmoa.store.enums.StoreRegistrationStatus;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRegistrationRepository extends JpaRepository<StoreRegistration, Long> {

    // 특정 유저가 이미 PENDING 상태의 신청을 했는지 확인
    boolean existsByApplicantAndStatus(User applicant, StoreRegistrationStatus status);

    boolean existsByApplicant(User user);

    Optional<StoreRegistration> findById(Long id);

    @Query(
            """
            select (count(sr.id) > 0)
            from StoreRegistration sr
            where sr.applicant.id = :applicantId
              and sr.status = :status
            """)
    boolean existsPending(
            @Param("applicantId") Long applicantId,
            @Param("status") StoreRegistrationStatus status);
}
