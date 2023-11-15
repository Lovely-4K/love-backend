package com.lovely4k.backend.couple.repository;

import com.lovely4k.backend.couple.Couple;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
    Optional<Couple> findByInvitationCode(String invitationCode);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select c from Couple c where c.id = :id")
    Optional<Couple> findByIdWithOptimisticLock(Long id);

    @Query(value = "SELECT * FROM couple WHERE id = ?1 AND deleted = true", nativeQuery = true)
    Optional<Couple> findDeletedById(Long coupleId);
}
