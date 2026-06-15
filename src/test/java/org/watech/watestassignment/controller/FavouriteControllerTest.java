package org.watech.watestassignment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.watech.watestassignment.exception.GlobalExceptionHandler;
import org.watech.watestassignment.service.AuthService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavouriteControllerTest {

    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new FavouriteController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getFavouritesWithoutAuthorizationHeaderReturnsUnauthorized() throws Exception {
        when(authService.extractToken(null)).thenReturn(null);
        when(authService.isAccessTokenValid(null)).thenReturn(false);

        mockMvc.perform(get("/favourites"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));

        verify(authService).extractToken(null);
        verify(authService).isAccessTokenValid(null);
    }

    @Test
    void getFavouritesWithValidBearerTokenReturnsStaticList() throws Exception {
        when(authService.extractToken("Bearer access-token")).thenReturn("access-token");
        when(authService.isAccessTokenValid("access-token")).thenReturn(true);

        mockMvc.perform(get("/favourites").header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Boba Fett"))
                .andExpect(jsonPath("$[4]").value("Wat Tambor"));

        verify(authService).extractToken("Bearer access-token");
        verify(authService).isAccessTokenValid("access-token");
    }
}