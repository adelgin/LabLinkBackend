package org.example.lablinkbackend.domain.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.social.model.dto.GroupRequestDto;
import org.example.lablinkbackend.domain.social.model.dto.GroupResponseDto;
import org.example.lablinkbackend.domain.social.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService groupService;

    @Operation(
            summary = "Создать группу",
            description = "Создаёт группу"
    )
    @PostMapping
    public ResponseEntity<GroupResponseDto> create(@RequestBody GroupRequestDto dto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.createGroup(dto, userDetails.getId()));
    }

    @Operation(
            summary = "Получить группу по id",
            description = "Получает json группы по её id"
    )
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @Operation(
            summary = "Получить все группы",
            description = "Получает все группы"
    )
    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> getAll() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @Operation(
            summary = "Удалить группу",
            description = "Удаляет группу"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        groupService.deleteGroup(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Подписаться на группу",
            description = "Оформляет подписку на посты группы"
    )
    @PostMapping("/{id}/subscribe")
    public ResponseEntity<String> subscribe(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        groupService.subscribeToGroup(id, userDetails.getId());
        return ResponseEntity.ok("Successfully subscribed to group");
    }
}
