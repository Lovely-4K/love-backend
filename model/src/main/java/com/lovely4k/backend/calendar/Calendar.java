package com.lovely4k.backend.calendar;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Calendar extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "member_id")
    private long ownerId;

    @Column(name = "schedule_type")
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Column(name = "schedule_details")
    private String scheduleDetails;

    private Calendar(LocalDate startDate, LocalDate endDate, long ownerId, ScheduleType scheduleType, String scheduleDetails) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.ownerId = ownerId;
        this.scheduleType = scheduleType;
        this.scheduleDetails = scheduleDetails;
    }

    public static Calendar create(LocalDate startDate, LocalDate endDate, long ownerId, ScheduleType scheduleType, String scheduleDetails) {
        if (scheduleType == ScheduleType.PERSONAL) {
            ownerId = 0L;
        }
        return new Calendar(startDate, endDate, ownerId, scheduleType, scheduleDetails);
    }
}