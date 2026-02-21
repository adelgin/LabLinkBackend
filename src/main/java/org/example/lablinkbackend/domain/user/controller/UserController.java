package org.example.lablinkbackend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.domain.user.model.dto.user.UserProfileDto;
import org.example.lablinkbackend.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Получить публичный профиль пользователя",
            description = "Получение всей основной информации о пользователе"
    )
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String username) {
        UserProfileDto profile = userService.getUserProfile(username);
        return ResponseEntity.ok(profile);
    }
}