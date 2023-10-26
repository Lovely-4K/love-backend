package com.lovely4k.backend.couple.repository;

import com.lovely4k.backend.couple.Couple;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
    Optional<Couple> findByInvitationCode(String invitationCode);
}