package org.example.lablinkbackend.domain.feed.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lablinkbackend.config.security.auth.UserDetailsImpl;
import org.example.lablinkbackend.domain.feed.model.dto.FeedContextDto;
import org.example.lablinkbackend.domain.feed.model.dto.PostResponseDto;
import org.example.lablinkbackend.domain.feed.service.FeedService;
import org.example.lablinkbackend.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

        log.info("=== FeedController.getFeed START ===");
        log.info("userId={}", userDetails.getId());
        log.info("page={}, size={}", page, size);

        FeedContextDto context = userService.getFeedContext(userDetails.getId());
        log.info("followingIds size={}", context.getFollowingIds() != null ? context.getFollowingIds().size() : 0);
        log.info("groupIds size={}", context.getGroupIds() != null ? context.getGroupIds().size() : 0);
        log.info("tagIds size={}", context.getTagIds() != null ? context.getTagIds().size() : 0);

        List<PostResponseDto> feed = feedService.getSmartFeed(
                context.getUserId(),
                context.getFollowingIds(),
                context.getGroupIds(),
                context.getTagIds(),
                page,
                size
        );

        log.info("feed size={}", feed.size());
        log.info("=== FeedController.getFeed END ===");
        return ResponseEntity.ok(feed);
    }
}