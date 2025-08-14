package kkukmoa.kkukmoa.store.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "store_like",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_user_store",
                        columnNames = {"user_id", "store_id"}))
public class StoreLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
