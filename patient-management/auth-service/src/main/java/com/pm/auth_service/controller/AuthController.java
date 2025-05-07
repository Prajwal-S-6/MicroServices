package com.pm.auth_service.controller;

import com.pm.auth_service.dto.UserLoginRequestDTO;
import com.pm.auth_service.dto.UserLoginResponseDTO;
import com.pm.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "user")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "UnAuthorized")})
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO loginRequestDTO) {
        Optional<String> authToken =  authService.authenticate(loginRequestDTO);
        if(authToken.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(new UserLoginResponseDTO(authToken.get()));
    }
}
