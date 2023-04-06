package com.example.catchingdata.services;

import com.example.catchingdata.dto.AuthDTO.AuthenticationResponse;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public AuthenticationResponse getUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found!");
        }
        var response = AuthenticationResponse.builder()
                .fullName(user.getFullName())
                .email(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        return response;
    }
}
