package com.wad3s.service_desk.security.jwt;

import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username = email
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // ← тут твой геттер из Role
                .toList();

        // Можно сделать свой класс, реализующий UserDetails,
        // но для простоты используем готовый org.springframework.security.core.userdetails.User
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())     // поле passwordHash из твоей сущности
                .authorities(authorities)
                .accountLocked(user.isLocked())       // isAccountNonLocked = !locked
                .disabled(!user.isEnabled())          // isEnabled = enabled
                .build();
    }
}
