package com.wad3s.service_desk.security.jwt;

import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SecurityUser implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // роли из ManyToMany
        Set<SimpleGrantedAuthority> roleAuthorities = user.getRoles().stream()
                .map(Role::getName)
                .map(this::toSpringRole)// допустим, у роли есть поле code = "ROLE_ADMIN"
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        // если хочешь дополнительно учитывать флаг is_admin:
        if (user.is_admin()) { // см. ниже про имя поля
            roleAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return roleAuthorities;
    }

    private String toSpringRole(String rawName) {
        if (rawName == null) {
            throw new IllegalStateException("Role name must not be null");
        }
        return rawName.startsWith("ROLE_") ? rawName : "ROLE_" + rawName;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Аккаунт не просрочен (если не используешь – можно всегда true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Заблокирован ли аккаунт
    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    // Пароль не просрочен (обычно тоже всегда true)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Включён ли пользователь
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public User getDomainUser() {
        return user;
    }
}
