package com.example.shiftmanagement.domain.absence;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AbsenceCreateRequest(
        @NotNull(message = "シフトIDは必須です")
        UUID shiftId,

        String reason
) {}