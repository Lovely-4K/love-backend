package com.lovely4k.backend.authentication;

import com.lovely4k.backend.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMember(Member member);

    Optional<RefreshToken> findByKeyValue(String keyValue);
}