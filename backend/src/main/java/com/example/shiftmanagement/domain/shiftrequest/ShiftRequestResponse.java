package com.example.shiftmanagement.domain.shiftrequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftRequestResponse(
        UUID id,
        UUID userId,
        String userName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime reviewedAt
) {
    public static ShiftRequestResponse from(ShiftRequest shiftRequest) {
        return new ShiftRequestResponse(
                shiftRequest.getId(),
                shiftRequest.getUser().getId(),
                shiftRequest.getUser().getName(),
                shiftRequest.getDate(),
                shiftRequest.getStartTime(),
                shiftRequest.getEndTime(),
                shiftRequest.getStatus().name(),
                shiftRequest.getRequestedAt(),
                shiftRequest.getReviewedAt()
        );
    }
}