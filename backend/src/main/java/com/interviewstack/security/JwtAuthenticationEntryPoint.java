package com.interviewstack.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewstack.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * httpBasic/formLogin을 쓰지 않으면 Spring Security 기본값(Http403ForbiddenEntryPoint)이
 * 적용되어 토큰 없이/잘못된 토큰으로 보호된 경로에 접근해도 본문 없는 403이 응답된다.
 * 이 EntryPoint를 SecurityConfig에 등록해 GlobalExceptionHandler와 동일한
 * ErrorResponse 형식의 401로 통일한다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse("인증이 필요합니다.", "UNAUTHENTICATED");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
