package com.teatreats.purchase.utils;

import com.teatreats.purchase.customexception.UnauthorizedException;
import com.teatreats.purchase.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class VerifyTokenAndReturnUserIdUtil {

    @Autowired
    private JWTService jwtService;

    public int validateToken(HttpServletRequest request) throws UnauthorizedException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token not passed.");
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtService.validateAndGetUserId(token);
    }

    public boolean validateAdminToken(HttpServletRequest request) throws UnauthorizedException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token not passed");
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtService.validateToken(token);
    }
}