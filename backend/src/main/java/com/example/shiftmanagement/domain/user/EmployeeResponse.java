package com.example.shiftmanagement.domain.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String email,
        String name,
        String role,
        Boolean isActive,
        LocalDateTime createdAt
) {
    public static EmployeeResponse from(User user) {
        return new EmployeeResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getIsActive(),
                user.getCreatedAt()
        );
    }
}