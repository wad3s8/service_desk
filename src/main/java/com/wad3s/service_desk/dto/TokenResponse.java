package com.wad3s.service_desk.dto;

public record TokenResponse(String accessToken, long expiresInSeconds) {}