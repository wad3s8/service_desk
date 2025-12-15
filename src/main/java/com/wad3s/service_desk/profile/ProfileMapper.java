package com.wad3s.service_desk.profile;

import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.domain.Role;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProfileMapper {

    public CustomerProfileDto toCustomer(User user) {
        return new CustomerProfileDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getLocation() != null
                        ? user.getLocation().getName()
                        : null,
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    public ExecutorProfileDto toExecutor(User user, String teamName) {
        return new ExecutorProfileDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                teamName
        );
    }

    public AhoManagerProfileDto toAhoManager(User user, String teamName) {
        return new AhoManagerProfileDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                teamName

        );
    }

    public SupervisorProfileDto toSupervisor(User user) {
        return new SupervisorProfileDto(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                user.getPhone()
        );
    }
}

