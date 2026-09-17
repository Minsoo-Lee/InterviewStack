package com.interviewstack.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService =
            new JwtService("test-only-secret-key-must-be-at-least-32-bytes-long", 60_000L);

    @Test
    void 토큰을_생성하고_이메일을_추출할_수_있다() {
        String token = jwtService.generateToken("user@example.com");

        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("user@example.com");
    }

    @Test
    void 유효하지_않은_토큰은_isTokenValid가_false를_반환한다() {
        assertThat(jwtService.isTokenValid("not-a-real-token")).isFalse();
    }
}
