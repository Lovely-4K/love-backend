package com.lovely4k.backend.couple;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Recovery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "couple_id")
    private Long coupleId;

    @Column(name = "requested_date")
    private LocalDate requestedDate;

    private Recovery(Long coupleId, LocalDate requestedDate) {
        this.coupleId = coupleId;
        this.requestedDate = requestedDate;
    }

    public static Recovery of(Long couple, LocalDate localDate) {
        return new Recovery(couple, localDate);
    }
}
