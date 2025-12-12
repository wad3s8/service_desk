package com.wad3s.service_desk.controller;

import com.wad3s.service_desk.domain.Role;
import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.dto.user.RegisterRequest;
import com.wad3s.service_desk.exception.EmailAlreadyExistsException;
import com.wad3s.service_desk.repository.RoleRepository;
import com.wad3s.service_desk.repository.UserRepository;
import com.wad3s.service_desk.service.AuthService;
import com.wad3s.service_desk.dto.user.LoginRequest;
import com.wad3s.service_desk.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {


        Role customerRole = roleRepository.findByName("customer")
                .orElseThrow(() -> new IllegalStateException("Role CUSTOMER not found in DB"));

        var u = new User();
        u.setEmail(request.email());
        u.setPasswordHash(passwordEncoder.encode(request.password()));
        u.setRoles(Set.of(customerRole));
        u.setFirstName(request.name());
        u.setLastName(request.lastName());
        u.setEnabled(true);
        u.setLocked(false);

        try {
            users.save(u);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest req,
                                               HttpServletRequest httpReq,
                                               HttpServletResponse httpRes) {
        return ResponseEntity.ok(authService.login(req.email(), req.password(), httpReq, httpRes));
    }


    /** Рефреш access по refresh-куке (ротация refresh внутри). */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest httpReq,
                                                 HttpServletResponse httpRes) {
        return ResponseEntity.ok(authService.refresh(httpReq, httpRes));
    }

    /** Логаут: отозвать refresh(и) и стереть куку. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication auth, HttpServletResponse httpRes) {
        User current = (auth != null && auth.getPrincipal() instanceof User u) ? u : null;
        authService.logout(current, httpRes);
        return ResponseEntity.ok().build();
    }
}
