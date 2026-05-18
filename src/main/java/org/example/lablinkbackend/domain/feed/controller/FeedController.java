package org.example.lablinkbackend.domain.feed.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.feed.model.dto.FeedContextDto;
import org.example.lablinkbackend.domain.feed.model.dto.PostResponseDto;
import org.example.lablinkbackend.domain.feed.service.FeedService;
import org.example.lablinkbackend.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final UserService userService;

    @Operation(
            summary = "Получит умную ленту пользователя",
            description = "Получает умную ленту пользователя с возможностью пагинации"
    )
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getFeed(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        FeedContextDto context = userService.getFeedContext(userDetails.getId());

        List<PostResponseDto> feed = feedService.getSmartFeed(
                context.getUserId(),
                context.getFollowingIds(),
                context.getGroupIds(),
                context.getTagIds(),
                page,
                size
        );

        return ResponseEntity.ok(feed);
    }
}