package org.watech.watestassignment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.watech.watestassignment.dto.LoginRequest;
import org.watech.watestassignment.dto.LoginResponse;
import org.watech.watestassignment.dto.RefreshRequest;
import org.watech.watestassignment.dto.RefreshResponse;
import org.watech.watestassignment.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public RefreshResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false)
                                       String authorizationHeader) {
        String accessToken = authService.extractToken(authorizationHeader);
        authService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }
}
