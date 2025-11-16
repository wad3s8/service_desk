package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Пользователь не аутентифицирован");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        throw new IllegalStateException("Неожиданный principal: " + principal);
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}