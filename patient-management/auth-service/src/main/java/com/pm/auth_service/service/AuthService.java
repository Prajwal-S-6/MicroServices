package com.pm.auth_service.service;

import com.pm.auth_service.Utils.JWTUtils;
import com.pm.auth_service.dto.UserLoginRequestDTO;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JWTUtils jwtUtils;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public Optional<String> authenticate(UserLoginRequestDTO loginRequest) {
        Optional<String> token = userService.findByEmail(loginRequest.email())
                .filter(usr -> passwordEncoder.matches(loginRequest.password(), usr.getPassword()))
                .map(usr -> jwtUtils.generateToken(usr.getEmail(), usr.getRole()));

        return token;
    }

    public boolean isValidToken(String token) {
        try {
            jwtUtils.validateToken(token);
            return true;
        } catch (JwtException jwtException) {
            return false;
        }
    }
}
