package com.example.catchingdata.dto.AuthDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String fullName;
    private String email;
    private String role;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;
}
