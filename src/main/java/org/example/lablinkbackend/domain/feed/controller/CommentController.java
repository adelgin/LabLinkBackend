package org.example.lablinkbackend.domain.feed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.feed.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Управление комментариями к публикациям")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Добавить новый комментарий",
            description = "Позволяет оставить текстовый комментарий под постом"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно добавлен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @PostMapping("/post/{postId}")
    public ResponseEntity<String> addComment(
            @Parameter(description = "ID поста") @PathVariable Long postId,
            @RequestBody String content,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.addComment(postId, userDetails.getId(), content);
        return ResponseEntity.ok("Comment added successfully");
    }

    @Operation(
            summary = "Обновить комментарий",
            description = "Позволяет автору изменить текст своего комментария"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Текст обновлен"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование чужого комментария"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(
            @Parameter(description = "ID комментария") @PathVariable Long commentId,
            @RequestBody String newContent,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.updateComment(commentId, userDetails.getId(), newContent);
        return ResponseEntity.ok("Comment updated successfully");
    }

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет комментарий из системы"
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @Parameter(description = "ID комментария") @PathVariable Long commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.ok("Comment deleted successfully");
    }
}