package com.lovely4k.backend.calendar;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

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

    @Column(name = "owner_id")
    private long ownerId;

    @Column(name = "couple_id")
    private long coupleId;

    @Column(name = "schedule_type")
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Column(name = "schedule_details")
    private String scheduleDetails;

    private Calendar(LocalDate startDate, LocalDate endDate, long ownerId, ScheduleType scheduleType, String scheduleDetails, long coupleId) {
        validateDates(startDate, endDate);
        validateScheduleType(scheduleType);
        this.ownerId = ownerId;
        this.coupleId = coupleId;
        this.scheduleDetails = validateScheduleDetails(scheduleDetails);
    }

    private String validateScheduleDetails(String details) {
        Validate.isTrue(StringUtils.isNotBlank(details), "scheduleDetails must not be blank");
        Validate.isTrue(details.length() <= 255, "scheduleDetails must be between 1 and 255 characters long");
        return details;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        this.startDate = Validate.notNull(startDate, "startDate must not be null");
        this.endDate = Validate.notNull(endDate, "endDate must not be null");
        Validate.isTrue(!startDate.isAfter(endDate), "endDate must be after startDate");
    }

    private void validateScheduleType(ScheduleType scheduleType) {
        this.scheduleType = Validate.notNull(scheduleType, "scheduleType must not be null");
    }

    public void update(LocalDate startDate, LocalDate endDate, String scheduleType, String scheduleDetails, Long ownerId) {
        validateDates(startDate, endDate);
        ScheduleType type = ScheduleType.valueOf(scheduleType.toUpperCase());
        this.ownerId = ownerId;
        if (type != ScheduleType.PERSONAL) {
            this.ownerId = 0L;
        }
        this.scheduleType = type;
        this.scheduleDetails = validateScheduleDetails(scheduleDetails);
    }

    public static Calendar create(LocalDate startDate, LocalDate endDate, long ownerId, String scheduleType, String scheduleDetails, long coupleId) {
        ScheduleType type = ScheduleType.valueOf(scheduleType.toUpperCase());

        if (type != ScheduleType.PERSONAL) {
            ownerId = 0L;
        }
        return new Calendar(startDate, endDate, ownerId, type, scheduleDetails, coupleId);
    }

}