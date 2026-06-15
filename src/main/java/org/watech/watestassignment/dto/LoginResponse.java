package org.watech.watestassignment.dto;

public record LoginResponse( String accessToken, String refreshToken, UserDto user) {
}
