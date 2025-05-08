package com.pm.auth_service.controller;

import com.pm.auth_service.dto.UserLoginRequestDTO;
import com.pm.auth_service.dto.UserLoginResponseDTO;
import com.pm.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "user")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
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
        if(authToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(new UserLoginResponseDTO(authToken.get()));
    }


    @GetMapping(value = "/validate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "UnAuthorized")})
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        // In the header--------- Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            LOG.info("Invalid Header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return authService.isValidToken(authHeader.substring(7)) ? ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
