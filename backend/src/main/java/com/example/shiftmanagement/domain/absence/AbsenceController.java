package com.example.shiftmanagement.domain.absence;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/absence-requests")
@RequiredArgsConstructor
public class AbsenceController {

    private final AbsenceService absenceService;

    @PostMapping
    public ResponseEntity<AbsenceResponse> create(
            Authentication authentication,
            @Valid @RequestBody AbsenceCreateRequest request
    ) {
        String email = authentication.getName();
        AbsenceResponse response = absenceService.create(email, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AbsenceResponse>> getMyRequests(Authentication authentication) {
        String email = authentication.getName();
        List<AbsenceResponse> responses = absenceService.getMyRequests(email);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AbsenceResponse>> getAllRequests() {
        List<AbsenceResponse> responses = absenceService.getAllRequests();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AbsenceResponse>> getPendingRequests() {
        List<AbsenceResponse> responses = absenceService.getPendingRequests();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<AbsenceResponse> approve(@PathVariable UUID id) {
        AbsenceResponse response = absenceService.approve(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<AbsenceResponse> reject(@PathVariable UUID id) {
        AbsenceResponse response = absenceService.reject(id);
        return ResponseEntity.ok(response);
    }
}