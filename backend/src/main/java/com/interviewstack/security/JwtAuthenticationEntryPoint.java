package com.interviewstack.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewstack.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 *
 * Boot 4부터는 Jackson 3가 기본이라 스프링이 더 이상 (Jackson 2) ObjectMapper 빈을
 * 자동 등록하지 않는다. 여기서는 jjwt-jackson이 어차피 classpath에 올려두는
 * Jackson 2 databind를 직접 사용하므로, 빈 주입 대신 이 클래스 전용 인스턴스를 직접 생성한다.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
