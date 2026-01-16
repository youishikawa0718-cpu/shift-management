package com.example.shiftmanagement.domain.shiftrequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shift-requests")
@RequiredArgsConstructor
public class ShiftRequestController {

    private final ShiftRequestService shiftRequestService;

    @PostMapping
    public ResponseEntity<ShiftRequestResponse> create(
            Authentication authentication,
            @Valid @RequestBody ShiftRequestCreateRequest request
    ) {
        String email = authentication.getName();
        ShiftRequestResponse response = shiftRequestService.create(email, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ShiftRequestResponse>> getMyRequests(Authentication authentication) {
        String email = authentication.getName();
        List<ShiftRequestResponse> responses = shiftRequestService.getMyRequests(email);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        String email = authentication.getName();
        shiftRequestService.delete(email, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ShiftRequestResponse>> getAllRequests() {
        List<ShiftRequestResponse> responses = shiftRequestService.getAllRequests();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ShiftRequestResponse>> getPendingRequests() {
        List<ShiftRequestResponse> responses = shiftRequestService.getPendingRequests();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ShiftRequestResponse> approve(@PathVariable UUID id) {
        ShiftRequestResponse response = shiftRequestService.approve(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ShiftRequestResponse> reject(@PathVariable UUID id) {
        ShiftRequestResponse response = shiftRequestService.reject(id);
        return ResponseEntity.ok(response);
    }
}