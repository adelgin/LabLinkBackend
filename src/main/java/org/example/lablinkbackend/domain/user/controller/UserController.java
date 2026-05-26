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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Получить публичный профиль пользователя по username",
            description = "Получение всей основной информации о пользователе по username"
    )
    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserProfileDto> getProfileByUsername(@PathVariable String username) {
        UserProfileDto profile = userService.getUserProfile(username);
        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "Получить публичный профиль пользователя по ID",
            description = "Получение всей основной информации о пользователе по ID"
    )
    @GetMapping("/by-id/{id}")
    public ResponseEntity<UserProfileDto> getProfileById(@PathVariable Long id) {
        UserProfileDto profile = userService.getUserProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "Получить публичный профиль пользователя (по username или id)",
            description = "Автоматически определяет тип параметра"
    )
    @GetMapping("/{identifier}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            return ResponseEntity.ok(userService.getUserProfileById(id));
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(userService.getUserProfile(identifier));
        }
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

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = userService.updateAvatar(userId, file);
            return ResponseEntity.ok(Map.of(
                    "avatarUrl", avatarUrl,
                    "fullUrl", "http://localhost:8081" + avatarUrl,
                    "message", "Avatar uploaded successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<?> deleteAvatar(@PathVariable Long userId) {
        userService.deleteAvatar(userId);
        return ResponseEntity.ok(Map.of("message", "Avatar deleted successfully"));
    }
}