package com.example.catchingdata.config.AuthConfig;

import com.example.catchingdata.models.UserModel.AccessToken;
import com.example.catchingdata.models.UserModel.RefreshToken;
import com.example.catchingdata.models.UserModel.User;
import com.example.catchingdata.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            generateResponse(
                    response,
                    "Token is not valid!",
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.getReasonPhrase()
            );
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUserEmail(jwt);

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            generateResponse(
                    response,
                    "User not found!",
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.getReasonPhrase()
            );
        }
        List<AccessToken> accessTokens = user.getAccessTokens();
        List<RefreshToken> refreshTokens = user.getRefreshTokens();

        boolean isTokenValid = accessTokens.stream()
                .map(token -> !token.isExpired() && !token.isRevoked())
                .anyMatch(result -> result == true);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        if (!isTokenValid || !jwtService.isValidToken(jwt, userDetails)) {
            generateResponse(
                    response,
                    "Token is not valid!",
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.getReasonPhrase()
            );
        } else {
            accessTokens.clear();
            refreshTokens.clear();
            SecurityContextHolder.clearContext();
            userRepository.save(user);
            generateResponse(
                    response,
                    "Logout success!",
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase()
            );
        }
    }

    private HttpServletResponse generateResponse(
            HttpServletResponse response,
            String message,
            Integer code,
            String status
    ) {
        response.setStatus(code);
        response.setContentType("application/json");
        Map<String, String> error = new HashMap<>();
        error.put("code", String.valueOf(code));
        error.put("status", status);
        error.put("message", message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(response.getWriter(), error);
            return response;
        } catch (IOException e) {
            throw new RuntimeException("Error writing response", e);
        }
    }
}
