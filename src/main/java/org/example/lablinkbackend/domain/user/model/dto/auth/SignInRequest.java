package org.example.lablinkbackend.domain.user.model.dto.auth;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}
