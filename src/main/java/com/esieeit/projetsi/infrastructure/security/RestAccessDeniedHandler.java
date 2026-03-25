package com.esieeit.projetsi.infrastructure.security;

import com.esieeit.projetsi.api.error.ErrorResponse;
import com.esieeit.projetsi.api.error.FieldErrorDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                new ErrorResponse(
                        Instant.now(),
                        HttpStatus.FORBIDDEN.value(),
                        "FORBIDDEN",
                        "You do not have permission to access this resource",
                        request.getRequestURI(),
                        List.<FieldErrorDetail>of()));
    }
}
