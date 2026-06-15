package org.watech.watestassignment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.watech.watestassignment.exception.UnauthorizedException;
import org.watech.watestassignment.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/favourites")
public class FavouriteController {

    private final AuthService authService;

    public FavouriteController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public List<String> getFavourites(@RequestHeader(value = "Authorization", required = false)
                                      String authorizationHeader) {
        String accessToken = authService.extractToken(authorizationHeader);
        if (!authService.isAccessTokenValid(accessToken)) {
            throw new UnauthorizedException("Unauthorized");
        }
        return List.of(
                "Boba Fett",
                "Bossk",
                "Lando Calrissian",
                "Lobot",
                "Wat Tambor"
        );
    }
}