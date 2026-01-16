package com.example.shiftmanagement.domain.shift;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping("/me")
    public ResponseEntity<List<ShiftResponse>> getMyShifts(Authentication authentication) {
        String email = authentication.getName();
        List<ShiftResponse> responses = shiftService.getMyShifts(email);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ShiftResponse>> getShiftsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ShiftResponse> responses = shiftService.getShiftsByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ShiftResponse>> getShiftsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ShiftResponse> responses = shiftService.getShiftsByDate(date);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/from-request/{shiftRequestId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ShiftResponse> createFromRequest(@PathVariable UUID shiftRequestId) {
        ShiftResponse response = shiftService.createFromRequest(shiftRequestId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/actual")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ShiftResponse> updateActual(
            @PathVariable UUID id,
            @RequestBody ShiftActualRequest request
    ) {
        ShiftResponse response = shiftService.updateActual(id, request);
        return ResponseEntity.ok(response);
    }
}