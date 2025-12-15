package com.wad3s.service_desk.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
public class SupervisorProfileDto implements ProfileDto {
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private String phone;
}
