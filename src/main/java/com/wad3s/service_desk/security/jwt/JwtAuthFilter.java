package com.wad3s.service_desk.security.jwt;


import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.repository.UserRepository;
import com.wad3s.service_desk.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String p = req.getRequestURI();
        return p.startsWith("/auth/")
                || p.startsWith("/swagger-ui/")
                || p.startsWith("/v3/api-docs")
                || p.equals("/actuator/health")
                || "OPTIONS".equalsIgnoreCase(req.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("JwtAuthFilter: " + method + " " + path);

        // пропускаем публичные эндпоинты, swagger и preflight
        if (path.startsWith("/auth/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || "OPTIONS".equalsIgnoreCase(method)) {

            System.out.println("JwtAuthFilter: skip " + path);
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        System.out.println("JwtAuthFilter: Authorization = " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Long userId = jwtService.extractUserId(token);
                System.out.println("JwtAuthFilter: userId = " + userId);

                User user = userRepository.findByIdWithRoles(userId).orElse(null);
                System.out.println("JwtAuthFilter: user = " + user);

                if (user != null && user.isEnabled() && !user.isLocked()) {
                    var authorities = user.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                            .toList();

                    var auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("JwtAuthFilter: authenticated as " + user.getEmail());
                } else {
                    System.out.println("JwtAuthFilter: user not found/disabled/locked");
                }
            } catch (JwtException | IllegalArgumentException e) {
                System.out.println("JwtAuthFilter: invalid token: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            System.out.println("JwtAuthFilter: no Bearer token");
        }

        chain.doFilter(request, response);
    }

}