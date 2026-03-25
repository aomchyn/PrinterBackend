package com.printer.myprinter.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.printer.myprinter.WebConfig;
import com.printer.myprinter.annotation.RequireAuth;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        // ข้าม OPTIONS request (preflight CORS)
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // ตรวจ annotation ที่ method level หรือ class level
        RequireAuth methodAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
        RequireAuth classAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
        RequireAuth requireAuth = methodAuth != null ? methodAuth : classAuth;

        if (requireAuth == null) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Missing or invalid Authorization header\"}");
            return false;
        }

        try {
            String tokenWithOutBearer = token.replace("Bearer ", "").trim();
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(WebConfig.getSecret()))
                    .build()
                    .verify(tokenWithOutBearer);

            // ดึง user id และ role จาก JWT
            String userId = decodedJWT.getSubject();
            String role = decodedJWT.getClaim("role").asString();

            // ตรวจ role ถ้ามีการกำหนดใน annotation
            String[] requiredRoles = requireAuth.roles();
            if (requiredRoles.length > 0) {
                boolean hasRequiredRole = Arrays.asList(requiredRoles).contains(role);
                if (!hasRequiredRole) {
                    log.warn("Access denied for user {} with role '{}'. Required roles: {}",
                            userId, role, Arrays.toString(requiredRoles));
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Access denied: insufficient permissions\"}");
                    return false;
                }
            }

            // เก็บ user info ไว้ใน request attributes เพื่อให้ controller ใช้ได้
            request.setAttribute("userId", userId);
            request.setAttribute("userRole", role);

            return true;
        } catch (Exception e) {
            log.warn("JWT verification failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
            return false;
        }
    }
}
