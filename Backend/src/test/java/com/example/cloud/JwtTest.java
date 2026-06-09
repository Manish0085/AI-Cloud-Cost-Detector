package com.example.cloud;

import com.example.cloud.security.JwtService;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTest {

    @Autowired
    private JwtService jwtService;

    @Test
    public void generateJwtTest() {
        String token =
                jwtService.generateToken(
                        "manish@gmail.com"
                );

        System.out.println(token);
    }
}
