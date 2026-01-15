package kr.blendit.api.auth.dto;

public record RefreshTokenInfo(String userUuid, int tokenVersion) {}
