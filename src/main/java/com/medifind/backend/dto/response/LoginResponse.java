package com.medifind.backend.dto.response;

import com.medifind.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String fullName;
    private String email;
    private Role role;
    private String token;
}