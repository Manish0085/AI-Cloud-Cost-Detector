package com.example.cloud.service.impl;

import com.example.cloud.entity.User;
import com.example.cloud.enums.AuthProvider;
import com.example.cloud.enums.Role;
import com.example.cloud.respository.UserRepo;
import com.example.cloud.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepo userRepository;
    private final JwtService jwtService;
    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication

    ) throws IOException {

        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken)
                        authentication;

        OAuth2User oauthUser =
                token.getPrincipal();

        String provider =
                token.getAuthorizedClientRegistrationId();

        User user;

        switch (provider.toLowerCase()) {

            case "google" ->

                    user = handleGoogleUser(
                            oauthUser
                    );

            case "github" ->

                    user = handleGithubUser(
                            oauthUser
                    );

            default ->

                    throw new IllegalStateException(
                            "Unsupported OAuth provider: "
                                    + provider
                    );
        }

        String jwt =
                jwtService.generateToken(
                        user.getEmail()
                );

        // Redirect to the configured frontend (app.frontend-url). In prod this
        // is the public HTTPS origin; in dev it stays http://localhost:5173.
        response.sendRedirect(
                frontendUrl + "/oauth-success?token=" + jwt
        );
    }

    private User handleGoogleUser(
            OAuth2User oauthUser
    ) {

        String email =
                oauthUser.getAttribute(
                        "email"
                );

        String name =
                oauthUser.getAttribute(
                        "name"
                );

        if(email == null || email.isBlank()) {
            throw new IllegalStateException(
                    "Email not provided by Google"
            );
        }

        return userRepository
                .findByEmail(email)
                .orElseGet(() ->

                        userRepository.save(

                                User.builder()
                                        .name(name)
                                        .email(email)
                                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                        .provider(AuthProvider.GOOGLE)
                                        .role(Role.USER)
                                        .enabled(true)
                                        .build()
                        )
                );
    }

    private User handleGithubUser(
            OAuth2User oauthUser
    ) {

        String email =
                oauthUser.getAttribute(
                        "email"
                );

        String username =
                oauthUser.getAttribute(
                        "login"
                );

        String name =
                oauthUser.getAttribute(
                        "name"
                );

        if (email == null || email.isBlank()) {
            Object githubIdObj =
                    oauthUser.getAttribute("id");

            if (githubIdObj == null) {

                throw new IllegalStateException(
                        "GitHub user id not found"
                );
            }

            String githubId =
                    githubIdObj.toString();
            email =
                    githubId +
                            "@github.local";
        }

        String finalEmail = email;

        return userRepository
                .findByEmail(finalEmail)
                .orElseGet(() ->

                        userRepository.save(

                                User.builder()
                                        .name(
                                                name != null
                                                        ? name
                                                        : username
                                        )
                                        .email(finalEmail)
                                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                        .role(Role.USER)
                                        .provider(AuthProvider.GITHUB)
                                        .enabled(true)
                                        .build()
                        )
                );
    }
}