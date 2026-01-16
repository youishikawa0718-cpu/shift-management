package com.example.shiftmanagement.domain.user;

public record EmployeeUpdateRequest(
        String name,
        Boolean isActive
) {}