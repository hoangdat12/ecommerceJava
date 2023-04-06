package com.example.catchingdata.controllers;

import com.example.catchingdata.dto.AuthDTO.*;
import com.example.catchingdata.services.AuthService;
import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            String msg = authService.register(request);
            return ResponseEntity.ok(msg);
        } catch (Exception exception) {
            throw new InternalException(exception.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthenticationRequest request) {
        try {
            AuthResponse authResponse = authService.login(request);
            return ResponseEntity.ok(authResponse);
        } catch (Exception exception) {
            throw new InternalException(exception.getMessage());
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenDTO> login(@RequestBody RefreshTokenRequest request) {
        try {
            String accessToken = authService.refreshToken(request.getRefreshToken());
            RefreshTokenDTO response = new RefreshTokenDTO(accessToken);
            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            throw new InternalException(exception.getMessage());
        }
    }
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test");
    }
}
