package com.medifind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String message;

}