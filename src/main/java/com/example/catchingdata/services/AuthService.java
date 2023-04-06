package com.example.catchingdata.services;

import com.example.catchingdata.config.AuthConfig.JwtService;
import com.example.catchingdata.config.AuthConfig.RefreshTokenService;
import com.example.catchingdata.dto.AuthDTO.AuthResponse;
import com.example.catchingdata.dto.AuthDTO.AuthenticationRequest;
import com.example.catchingdata.dto.AuthDTO.AuthenticationResponse;
import com.example.catchingdata.dto.AuthDTO.RegisterRequest;
import com.example.catchingdata.models.UserModel.AccessToken;
import com.example.catchingdata.models.UserModel.RefreshToken;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CartService cartService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
//    private final ObjectValidate<RegisterRequest> registerValidate;
//    private final ObjectValidate<AuthenticationRequest> authenticationValidate;
    public String register(RegisterRequest request) {
//        var validations = registerValidate.validate(request);
//        if (!validations.isEmpty()) {
//            return String.join("|", validations);
//        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new IllegalArgumentException("Don't match password!");
        }
        User userExist = userRepository.findByEmail(request.getEmail())
                .orElse(null);
        if (userExist != null) {
            throw new IllegalArgumentException("Email is Exist in System!");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .isActive(true)
                .accessTokens(new ArrayList<>())
                .refreshTokens(new ArrayList<>())
                .build();
        userRepository.save(user);
        cartService.createCart(user);
        return "Register successfully!";
    }

    public AuthResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        revokedAllUserTokensWithAccessToken(user);
        AuthenticationResponse response = AuthenticationResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        String token = jwtService.generateToken(user, user);
        String refreshToken = refreshTokenService.generateToken(user, user);
        saveUserAccessToken(user, token);
        saveRefreshToken(user, refreshToken);
        userRepository.save(user);
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .data(response)
                .build();
    }
    public String refreshToken(String refreshToken) {
        String userEmail = refreshTokenService.extractUserEmail(refreshToken);
        if (userEmail == null) {
            throw new BadCredentialsException("Invalid refresh Token");
        }
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            throw new BadCredentialsException("Invalid refresh Token");
        }

        List<RefreshToken> refreshTokens = user.getRefreshTokens();
        List<AccessToken> accessTokens = user.getAccessTokens();

        boolean isTokenUsed = refreshTokens.stream()
                .map(token -> token.isExpired() || token.isRevoked())
                .anyMatch(result -> result == true);
        if (isTokenUsed) {
            log.info("{} that used used token!", userEmail);
            accessTokens.clear();
            refreshTokens.clear();
            userRepository.save(user);
            return "Something wrong happened, Please relogin to continue using Website";
        }
        else {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            boolean isValidRefreshToken = refreshTokenService.isValidToken(refreshToken, userDetails);
            RefreshToken holderToken = refreshTokens.stream()
                    .filter(refreshToken1 -> refreshToken1.getToken().equals(refreshToken))
                    .findFirst()
                    .orElse(null);
            if (!isValidRefreshToken && holderToken != null) {
                throw new BadCredentialsException("Invalid refresh Token");
            }
            revokedAllUserTokensWithAccessToken(user);

            String newAccessToken = jwtService.generateToken(userDetails, user);
            String newRefreshToken = refreshTokenService.generateToken(userDetails, user);
            saveUserAccessToken(user, newAccessToken);
            saveRefreshToken(user, newRefreshToken);

            holderToken.setRevoked(true);
            holderToken.setExpired(true);
            userRepository.save(user);
            holderToken.setToken(newRefreshToken);
            return newAccessToken;
        }
    }

    // PRIVATE
    private void saveUserAccessToken(User user, String jwtToken) {
        var token = AccessToken.builder()
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        user.getAccessTokens().add(token);
    }
    private void saveRefreshToken(User user, String jwtToken) {
        var token = RefreshToken.builder()
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        user.getRefreshTokens().add(token);
    }
    private void revokedAllUserTokensWithAccessToken (User user) {
        List<AccessToken> validUserTokens = user.getAccessTokens();
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        userRepository.save(user);
    }
    private void revokedAllUserTokensWithRefreshToken (User user) {
        List<RefreshToken> validUserTokens = user.getRefreshTokens();
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        userRepository.save(user);
    }
}
