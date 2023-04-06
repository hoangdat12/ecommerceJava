package com.example.catchingdata.dto.AuthDTO;

import com.example.catchingdata.dto.AuthDTO.AuthenticationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private AuthenticationResponse data;
}
