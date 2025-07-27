package kkukmoa.kkukmoa.stamp.repository;

import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StampRepository extends JpaRepository<Stamp, Long> {

    List<Stamp> findByStore(Store store);

    Optional<Stamp> findByUserAndStore(User user, Store store);
}
