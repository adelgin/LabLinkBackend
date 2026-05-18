package org.example.lablinkbackend.domain.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.social.model.dto.FriendRequestDto;
import org.example.lablinkbackend.domain.social.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/friends")
@RestController
@AllArgsConstructor
@Tag(name = "Social & Friends", description = "Управление связями между учеными (друзья, подписки)")
public class FriendshipController {

    private final FriendshipService friendshipService;



    @Operation(
            summary = "Отправить запрос в друзья",
            description = "Создает заявку на дружбу с другим пользователем"
    )
    @ApiResponse(responseCode = "200", description = "Запрос отправлен")
    @PostMapping("/request/{friendId}")
    public ResponseEntity<?> sendRequest(
            @Parameter(description = "ID будущего друга") @PathVariable Long friendId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        friendshipService.sendFriendRequest(userDetails.getId(), friendId);
        return ResponseEntity.ok("Friend request sent");
    }

    @Operation(
            summary = "Принять запрос в друзья",
            description = "Подтверждает входящую заявку на дружбу"
    )
    @PostMapping("/accept/{friendId}")
    public ResponseEntity<?> acceptRequest(
            @Parameter(description = "ID того, кто отправил заявку") @PathVariable Long friendId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        friendshipService.acceptFriendRequest(userDetails.getId(), friendId);
        return ResponseEntity.ok("Friend request accepted");
    }

    @Operation(
            summary = "Удалить из друзей / Отменить запрос",
            description = "Разрывает дружескую связь или отменяет поданную заявку"
    )
    @DeleteMapping("/delete/{friendId}")
    public ResponseEntity<?> removeRequest(
            @Parameter(description = "ID пользователя") @PathVariable Long friendId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        friendshipService.removeFriendshipRequest(userDetails.getId(), friendId);
        return ResponseEntity.ok("You are not friends now!");
    }

    @GetMapping("/requests/incoming")
    public ResponseEntity<List<FriendRequestDto>> getIncoming(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(friendshipService.getIncomingRequests(userDetails.getId()));
    }
}