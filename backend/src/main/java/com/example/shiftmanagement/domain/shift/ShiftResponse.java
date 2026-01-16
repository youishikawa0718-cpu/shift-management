package com.example.shiftmanagement.domain.shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftResponse(
        UUID id,
        UUID userId,
        String userName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        LocalTime actualStartTime,
        LocalTime actualEndTime,
        LocalDateTime createdAt
) {
    public static ShiftResponse from(Shift shift) {
        return new ShiftResponse(
                shift.getId(),
                shift.getUser().getId(),
                shift.getUser().getName(),
                shift.getDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getActualStartTime(),
                shift.getActualEndTime(),
                shift.getCreatedAt()
        );
    }
}