package com.example.catchingdata.controllers;

import com.example.catchingdata.dto.AuthDTO.AuthenticationResponse;
import com.example.catchingdata.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/{userId}")
    public ResponseEntity<AuthenticationResponse> getUser(@PathVariable String userId) {
        var response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }
}
