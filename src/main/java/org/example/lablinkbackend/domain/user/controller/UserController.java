package org.example.lablinkbackend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.user.model.dto.user.UserCardDto;
import org.example.lablinkbackend.domain.user.model.dto.user.UserProfileDto;
import org.example.lablinkbackend.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @Operation(
            summary = "Поиск по пользователя"
    )
    @GetMapping("/search")
    public ResponseEntity<Page<UserCardDto>> searchUsers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String query,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(userDetails.getId(), query, pageable));
    }
}