package org.example.lablinkbackend.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.domain.feed.model.dto.PostResponseDto;
import org.example.lablinkbackend.domain.feed.model.entity.Post;
import org.example.lablinkbackend.domain.feed.repository.PostCommentRepository;
import org.example.lablinkbackend.domain.feed.repository.PostLikeRepository;
import org.example.lablinkbackend.domain.feed.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostService postService;

    public List<PostResponseDto> getSmartFeed(Long userId, List<Long> friendIds, List<Long> groupIds, List<Integer> userTagIds, int page, int size) {
        // 1. Окно выборки: 7 дней (баланс между актуальностью и глубиной)
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);

        // 2. Достаем всех кандидатов
        List<Post> candidates = postRepository.findFeedCandidates(friendIds, groupIds, userTagIds, threshold);

        // 3. Ранжируем и пагинируем
        return candidates.stream()
                .map(post -> {
                    double score = calculateWeight(post, userId, friendIds, groupIds, userTagIds);
                    PostResponseDto dto = postService.convertToDto(post, userId);
                    dto.setRecommendationScore(score);
                    return dto;
                })
                .sorted(Comparator.comparing(PostResponseDto::getRecommendationScore).reversed())
                // Механика бесконечной ленты
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    private double calculateWeight(Post post, Long userId, List<Long> friends, List<Long> groups, List<Integer> userTags) {
        double weight = 1.0;

        // Социальные связи
        if (friends.contains(post.getAuthor().getId())) weight += 100.0;
        if (post.getGroup() != null && groups.contains(post.getGroup().getId())) weight += 50.0;

        // Теги (научные интересы)
        if (post.getAuthor().getTags() != null) {
            long matches = post.getAuthor().getTags().stream()
                    .filter(tag -> userTags.contains(tag.getId()))
                    .count();
            weight += (matches * 40.0);
        }

        // Популярность (Engagement)
        long likes = postLikeRepository.countByPostId(post.getId());
        long comments = postCommentRepository.countByPostId(post.getId());
        long shares = postRepository.countByParentPostId(post.getId());
        double engagement = likes + (comments * 5.0) + (shares * 10.0);

        // Фактор времени (Hours passed)
        long hours = ChronoUnit.HOURS.between(post.getCreatedAt(), LocalDateTime.now());

        // LabLink Alpha Formula
        return (weight + Math.log1p(engagement)) / Math.pow(hours + 2.0, 1.5);
    }
}
