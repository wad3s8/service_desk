package com.wad3s.service_desk.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class CustomerProfileDto implements ProfileDto {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String officeName;
    private Set<String> roles;
}
