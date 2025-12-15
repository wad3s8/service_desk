package com.wad3s.service_desk.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ProfileDto getMyProfile() {
        return profileService.getProfile();
    }

    @PatchMapping
    public ProfileDto updateProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return profileService.updateProfile(request);
    }
}
