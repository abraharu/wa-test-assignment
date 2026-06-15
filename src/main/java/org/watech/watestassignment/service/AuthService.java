package org.watech.watestassignment.service;

import org.springframework.stereotype.Service;
import org.watech.watestassignment.dto.LoginRequest;
import org.watech.watestassignment.dto.LoginResponse;
import org.watech.watestassignment.dto.RefreshResponse;
import org.watech.watestassignment.dto.UserDto;
import org.watech.watestassignment.exception.UnauthorizedException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final Map<String, String> accessTokens = new ConcurrentHashMap<>();

    private final Map<String, String> refreshTokens = new ConcurrentHashMap<>();

    public LoginResponse login(LoginRequest request) {
        String username = request.username();
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        accessTokens.put(accessToken, username);
        refreshTokens.put(refreshToken, username);
        return new LoginResponse(accessToken, refreshToken, new UserDto(username));
    }

    public RefreshResponse refresh(String refreshToken) {
        String username = refreshTokens.get(refreshToken);
        if (username == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        accessTokens.entrySet().removeIf(entry -> entry.getValue().equals(username));
        String newAccessToken = UUID.randomUUID().toString();

        accessTokens.put(newAccessToken, username);
        return new RefreshResponse(newAccessToken);
    }

    public void logout(String accessToken) {
        if (accessToken == null) {
            return;
        }
        accessTokens.remove(accessToken);
    }

    public boolean isAccessTokenValid(String accessToken) {
        return accessToken != null && accessTokens.containsKey(accessToken);
    }

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }
}
