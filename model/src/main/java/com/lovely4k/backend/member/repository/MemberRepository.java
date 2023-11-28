package com.lovely4k.backend.member.repository;

import com.lovely4k.backend.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("select m from Member m where m.id = :boyId or m.id = :girlId")
    List<Member> findCoupleMembersById(@Param("boyId") Long boyId, @Param("girlId") Long girlId);
}
