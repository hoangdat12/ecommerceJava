package com.example.catchingdata.dto.AuthDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "The full name is required!")
    private String fullName;
    @Email(message = "The email is not valid!")
    @NotBlank(message = "The email is required!")
    private String email;
    @NotBlank(message = "The password is required!")
    private String password;
    @NotBlank(message = "The rePassword is required!")
    private String rePassword;
}
