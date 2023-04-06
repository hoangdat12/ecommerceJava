package com.example.catchingdata.dto.AuthDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotNull(message = "The email should not be null!")
    @NotEmpty(message = "The email should not be empty!")
    private String email;
    @NotNull(message = "The password should not be null!")
    @NotEmpty(message = "The password should not be empty!")
    private String password;
}
