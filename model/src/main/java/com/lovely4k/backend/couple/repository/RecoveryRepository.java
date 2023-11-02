package com.lovely4k.backend.couple.repository;

import com.lovely4k.backend.couple.Recovery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecoveryRepository extends JpaRepository<Recovery, Long> {
    Optional<Recovery> findByCoupleId(Long coupleId);
}
