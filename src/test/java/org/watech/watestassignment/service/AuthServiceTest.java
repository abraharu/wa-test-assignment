package org.watech.watestassignment.service;

import org.junit.jupiter.api.Test;
import org.watech.watestassignment.dto.LoginRequest;
import org.watech.watestassignment.dto.LoginResponse;
import org.watech.watestassignment.dto.RefreshResponse;
import org.watech.watestassignment.exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @Test
    void loginCreatesAccessAndRefreshTokensForUser() {
        LoginResponse response = authService.login(new LoginRequest("luke", "secret"));

        assertAll(
                () -> assertNotNull(response.accessToken()),
                () -> assertNotNull(response.refreshToken()),
                () -> assertEquals("luke", response.user().username()),
                () -> assertTrue(authService.isAccessTokenValid(response.accessToken()))
        );
    }

    @Test
    void refreshCreatesNewValidAccessTokenForValidRefreshToken() {
        LoginResponse login = authService.login(new LoginRequest("leia", "secret"));

        RefreshResponse refresh = authService.refresh(login.refreshToken());

        assertAll(
                () -> assertNotNull(refresh.accessToken()),
                () -> assertNotEquals(login.accessToken(), refresh.accessToken()),
                () -> assertFalse(authService.isAccessTokenValid(login.accessToken())),
                () -> assertTrue(authService.isAccessTokenValid(refresh.accessToken()))
        );
    }

    @Test
    void refreshRejectsUnknownRefreshToken() {
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.refresh("missing-token")
        );

        assertEquals("Invalid refresh token", exception.getMessage());
    }

    @Test
    void logoutRemovesOnlyProvidedAccessToken() {
        LoginResponse first = authService.login(new LoginRequest("han", "secret"));
        LoginResponse second = authService.login(new LoginRequest("chewie", "secret"));

        authService.logout(first.accessToken());

        assertAll(
                () -> assertFalse(authService.isAccessTokenValid(first.accessToken())),
                () -> assertTrue(authService.isAccessTokenValid(second.accessToken()))
        );
    }

    @Test
    void logoutIgnoresNullToken() {
        LoginResponse login = authService.login(new LoginRequest("yoda", "secret"));

        authService.logout(null);

        assertTrue(authService.isAccessTokenValid(login.accessToken()));
    }

    @Test
    void extractTokenReturnsBearerToken() {
        assertEquals("abc-123", authService.extractToken("Bearer abc-123"));
    }

    @Test
    void extractTokenReturnsNullForMissingOrUnsupportedHeader() {
        assertAll(
                () -> assertNull(authService.extractToken(null)),
                () -> assertNull(authService.extractToken("Basic abc-123")),
                () -> assertNull(authService.extractToken("Bearer"))
        );
    }

    @Test
    void isAccessTokenValidReturnsFalseForNullAndUnknownToken() {
        assertAll(
                () -> assertFalse(authService.isAccessTokenValid(null)),
                () -> assertFalse(authService.isAccessTokenValid("missing-token"))
        );
    }
}
