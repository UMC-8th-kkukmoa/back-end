package kkukmoa.kkukmoa.stamp.repository;

import java.util.List;
import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampRepository extends JpaRepository<Stamp, Long> {

  List<Stamp> findByStore(Store store);

}
