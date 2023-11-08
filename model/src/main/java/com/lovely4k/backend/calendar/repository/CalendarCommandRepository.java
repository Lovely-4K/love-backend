package com.lovely4k.backend.calendar.repository;

import com.lovely4k.backend.calendar.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarCommandRepository extends JpaRepository<Calendar, Long> {
}