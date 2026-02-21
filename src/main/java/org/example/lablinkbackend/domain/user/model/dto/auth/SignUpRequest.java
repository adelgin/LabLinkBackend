package org.example.lablinkbackend.domain.user.model.dto.auth;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
}
