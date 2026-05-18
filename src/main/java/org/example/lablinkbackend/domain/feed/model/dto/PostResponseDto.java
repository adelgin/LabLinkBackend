package org.example.lablinkbackend.domain.feed.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long authorId;
    private String authorName;
    private Long groupId;
    private String groupName;
    private boolean isGroupPost;
    private List<String> fileUrls;

    private PostResponseDto parentPost;

    private long likesCount;
    private boolean isLikedByMe;
    private long commentsCount;
    private long sharesCount;

    private List<CommentResponseDto> comments;
    private double recommendationScore; // Вес данного поста (его рекомендательный рейтинг)

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentResponseDto {
        private Long id;
        private Long authorId;
        private String authorName;
        private String content;
        private LocalDateTime createdAt;
    }
}