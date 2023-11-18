package com.lovely4k.backend.authentication;

import com.lovely4k.backend.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    private String id;

    @JoinColumn(name = "member_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(name = "key_value",nullable = false)
    private String keyValue;

    @Override
    public String toString() {
        return "RefreshToken{" +
            "id='" + id + '\'' +
            ", member=" + member +
            ", keyValue='" + keyValue + '\'' +
            '}';
    }
}
