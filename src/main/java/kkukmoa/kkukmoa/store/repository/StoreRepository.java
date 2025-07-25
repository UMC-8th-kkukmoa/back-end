package kkukmoa.kkukmoa.store.repository;

import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByMerchantNumber(String merchantNumber);

    Optional<Store> findByOwner(User owner);
}
