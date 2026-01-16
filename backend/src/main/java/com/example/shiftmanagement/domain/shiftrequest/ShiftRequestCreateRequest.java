package com.example.shiftmanagement.domain.shiftrequest;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftRequestCreateRequest(
        @NotNull(message = "日付は必須です")
        LocalDate date,

        @NotNull(message = "開始時刻は必須です")
        LocalTime startTime,

        @NotNull(message = "終了時刻は必須です")
        LocalTime endTime
) {}