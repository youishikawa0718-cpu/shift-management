package com.example.shiftmanagement.domain.shift;

import java.time.LocalTime;

public record ShiftActualRequest(
        LocalTime actualStartTime,
        LocalTime actualEndTime
) {}