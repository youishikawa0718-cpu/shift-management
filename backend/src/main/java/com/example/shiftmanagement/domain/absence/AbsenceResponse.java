package com.example.shiftmanagement.domain.absence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record AbsenceResponse(
        UUID id,
        UUID userId,
        String userName,
        UUID shiftId,
        LocalDate shiftDate,
        LocalTime shiftStartTime,
        LocalTime shiftEndTime,
        String reason,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime reviewedAt
) {
    public static AbsenceResponse from(AbsenceRequest absenceRequest) {
        return new AbsenceResponse(
                absenceRequest.getId(),
                absenceRequest.getUser().getId(),
                absenceRequest.getUser().getName(),
                absenceRequest.getShift() != null ? absenceRequest.getShift().getId() : null,
                absenceRequest.getShift() != null ? absenceRequest.getShift().getDate() : null,
                absenceRequest.getShift() != null ? absenceRequest.getShift().getStartTime() : null,
                absenceRequest.getShift() != null ? absenceRequest.getShift().getEndTime() : null,
                absenceRequest.getReason(),
                absenceRequest.getStatus().name(),
                absenceRequest.getRequestedAt(),
                absenceRequest.getReviewedAt()
        );
    }
}