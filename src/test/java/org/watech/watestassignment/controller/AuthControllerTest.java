package org.watech.watestassignment.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.watech.watestassignment.dto.LoginRequest;
import org.watech.watestassignment.dto.LoginResponse;
import org.watech.watestassignment.dto.RefreshRequest;
import org.watech.watestassignment.dto.RefreshResponse;
import org.watech.watestassignment.dto.UserDto;
import org.watech.watestassignment.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginDelegatesToAuthService() {
        LoginRequest request = new LoginRequest("luke", "secret");
        LoginResponse expected = new LoginResponse("access", "refresh", new UserDto("luke"));
        when(authService.login(request)).thenReturn(expected);

        LoginResponse response = authController.login(request);

        assertSame(expected, response);
        verify(authService).login(request);
    }

    @Test
    void refreshDelegatesRefreshTokenToAuthService() {
        RefreshRequest request = new RefreshRequest("refresh-token");
        RefreshResponse expected = new RefreshResponse("new-access-token");
        when(authService.refresh("refresh-token")).thenReturn(expected);

        RefreshResponse response = authController.refresh(request);

        assertSame(expected, response);
        verify(authService).refresh("refresh-token");
    }

    @Test
    void logoutExtractsBearerTokenAndReturnsNoContent() {
        when(authService.extractToken("Bearer access-token")).thenReturn("access-token");

        ResponseEntity<Void> response = authController.logout("Bearer access-token");

        assertEquals(204, response.getStatusCode().value());
        verify(authService).extractToken("Bearer access-token");
        verify(authService).logout("access-token");
    }

    @Test
    void logoutPassesNullTokenWhenAuthorizationHeaderIsMissing() {
        when(authService.extractToken(null)).thenReturn(null);

        ResponseEntity<Void> response = authController.logout(null);

        assertEquals(204, response.getStatusCode().value());
        verify(authService).extractToken(null);
        verify(authService).logout(null);
    }
}