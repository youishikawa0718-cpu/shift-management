package com.example.shiftmanagement.domain.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String email,
        String name,
        String role
) {}