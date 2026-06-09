package com.example.cloud.service.impl;

import com.example.cloud.dto.AuthResponse;
import com.example.cloud.dto.CurrentUserResponse;
import com.example.cloud.dto.LoginRequest;
import com.example.cloud.dto.RegisterRequest;
import com.example.cloud.entity.User;
import com.example.cloud.enums.AuthProvider;
import com.example.cloud.enums.Role;
import com.example.cloud.exception.EmailAlreadyExistsException;
import com.example.cloud.exception.InvalidCredentialException;
import com.example.cloud.exception.UserNotFoundException;
import com.example.cloud.respository.UserRepo;
import com.example.cloud.security.JwtService;
import com.example.cloud.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepo.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "User already exists with email: " + request.email()
            );
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .provider(AuthProvider.LOCAL)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userRepo.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(
                token,
                "User registered successfully"
        );

    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepo.
                findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialException("Invalid email or password"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new InvalidCredentialException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(
                request.email()
        );

        return new AuthResponse(
                token,
                "Login successful"
        );
    }

    @Override
    public CurrentUserResponse getCurrentLoggedInUser(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new CurrentUserResponse(
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
