package org.example.lablinkbackend.domain.feed.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.feed.model.dto.PostRequestDto;
import org.example.lablinkbackend.domain.feed.model.dto.PostResponseDto;
import org.example.lablinkbackend.domain.feed.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Создать пост",
            description = "Создает пост с возможностью прикрепления файлов"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("post") PostRequestDto dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(postService.createPostWithFiles(dto, files, userDetails.getId()));
    }

    @Operation(
            summary = "Добавить комментарий к посту",
            description = "Добавление комментария к посту"
    )
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long postId,
                                             @RequestBody String content,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.addComment(postId, userDetails.getId(), content);
        return ResponseEntity.ok("Comment added successfully");
    }

    @Operation(
            summary = "Получить посты пользователя",
            description = "Получение постов пользователя (с проверкой лайков текущего юзера)"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponseDto>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Long currentUserId = (userDetails != null) ? userDetails.getId() : null;
        return ResponseEntity.ok(postService.getUserPosts(userId, pageable, currentUserId));
    }

    @Operation(
            summary = "Получить посты группы",
            description = "Получение постов группы"
    )
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<PostResponseDto>> getGroupPosts(
            @PathVariable Long groupId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long currentUserId = (userDetails != null) ? userDetails.getId() : null;
        return ResponseEntity.ok(postService.getGroupPosts(groupId, pageable, currentUserId));
    }

    @Operation(
            summary = "Удалить вложение из поста",
            description = "Удаляет файл-вложение из поста"
    )
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<String> deleteAttachment(
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        postService.deleteAttachment(attachmentId, userDetails.getId());
        return ResponseEntity.ok("Attachment deleted successfully");
    }
}