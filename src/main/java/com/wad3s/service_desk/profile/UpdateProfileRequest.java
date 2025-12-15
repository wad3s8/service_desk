package com.wad3s.service_desk.profile;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Pattern(
            regexp = "^[+\\d][\\d\\s()\\-]{6,30}$",
            message = "Некорректный номер телефона"
    )
    private String phone;
}

