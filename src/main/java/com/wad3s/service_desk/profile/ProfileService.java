package com.wad3s.service_desk.profile;

import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.repository.TeamMemberRepository;
import com.wad3s.service_desk.repository.UserRepository;
import com.wad3s.service_desk.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public ProfileDto getProfile() {
        String email = currentUserService.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (hasRole(user, "customer")) {
            return profileMapper.toCustomer(user);
        }

        if (hasRole(user, "executor")) {

            String teamName = teamMemberRepository
                    .findByUserIdAndActiveTrue(user.getId())
                    .map(tm -> tm.getTeam().getName())
                    .orElse(null);

            return profileMapper.toExecutor(user, teamName);
        }

        if (hasRole(user, "aho_head")) {

            String teamName = teamMemberRepository
                    .findByUserIdAndActiveTrue(user.getId())
                    .map(tm -> tm.getTeam().getName())
                    .orElse(null);

            return profileMapper.toAhoManager(user, teamName);


        }

        if (hasRole(user, "supervisor")) {
            return profileMapper.toSupervisor(user);
        }

        throw new IllegalStateException("Роль пользователя не поддерживается");
    }

    @Transactional
    public ProfileDto updateProfile(UpdateProfileRequest request) {

        String email = currentUserService.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        userRepository.save(user);

        return getProfile();
    }

    private boolean hasRole(User user, String role) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(role));
    }
}
