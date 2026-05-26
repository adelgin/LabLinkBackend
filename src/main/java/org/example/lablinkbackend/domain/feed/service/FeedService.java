package org.example.lablinkbackend.domain.feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lablinkbackend.domain.feed.model.dto.PostResponseDto;
import org.example.lablinkbackend.domain.feed.model.entity.Post;
import org.example.lablinkbackend.domain.feed.repository.PostCommentRepository;
import org.example.lablinkbackend.domain.feed.repository.PostLikeRepository;
import org.example.lablinkbackend.domain.feed.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostService postService;

    public List<PostResponseDto> getSmartFeed(Long userId, List<Long> friendIds, List<Long> groupIds, List<Integer> userTagIds, int page, int size) {
        log.info("=== getSmartFeed START ===");
        log.info("userId={}", userId);
        log.info("friendIds={}", friendIds);
        log.info("groupIds={}", groupIds);
        log.info("userTagIds={}", userTagIds);
        log.info("page={}, size={}", page, size);

        try {
            List<Long> safeFriendIds = friendIds != null ? friendIds : new ArrayList<>();
            List<Long> safeGroupIds = groupIds != null ? groupIds : new ArrayList<>();
            List<Integer> safeUserTagIds = userTagIds != null ? userTagIds : new ArrayList<>();

            log.info("safeFriendIds={}", safeFriendIds);
            log.info("safeGroupIds={}", safeGroupIds);
            log.info("safeUserTagIds={}", safeUserTagIds);

            if (safeFriendIds.isEmpty() && safeGroupIds.isEmpty() && safeUserTagIds.isEmpty()) {
                log.warn("All lists empty - returning empty feed");
                return new ArrayList<>();
            }

            LocalDateTime threshold = LocalDateTime.now().minusDays(7);
            log.info("threshold={}", threshold);

            List<Post> candidates = postRepository.findFeedCandidates(safeFriendIds, safeGroupIds, safeUserTagIds, threshold);
            log.info("candidates size={}", candidates.size());

            List<PostResponseDto> result = candidates.stream()
                    .map(post -> {
                        double score = calculateWeight(post, userId, safeFriendIds, safeGroupIds, safeUserTagIds);
                        PostResponseDto dto = postService.convertToDto(post, userId);
                        dto.setRecommendationScore(score);
                        return dto;
                    })
                    .sorted(Comparator.comparing(PostResponseDto::getRecommendationScore).reversed())
                    .skip((long) page * size)
                    .limit(size)
                    .collect(Collectors.toList());

            log.info("result size={}", result.size());
            log.info("=== getSmartFeed END ===");
            return result;

        } catch (Exception e) {
            log.error("ERROR in getSmartFeed: ", e);
            throw new RuntimeException("Feed error: " + e.getMessage(), e);
        }
    }

    private double calculateWeight(Post post, Long userId, List<Long> friends, List<Long> groups, List<Integer> userTags) {
        double weight = 1.0;

        if (friends != null && !friends.isEmpty() && friends.contains(post.getAuthor().getId())) {
            weight += 100.0;
        }

        if (groups != null && !groups.isEmpty() && post.getGroup() != null && groups.contains(post.getGroup().getId())) {
            weight += 50.0;
        }

        if (userTags != null && !userTags.isEmpty() && post.getAuthor().getTags() != null) {
            long matches = post.getAuthor().getTags().stream()
                    .filter(tag -> userTags.contains(tag.getId()))
                    .count();
            weight += (matches * 40.0);
        }

        long likes = postLikeRepository.countByPostId(post.getId());
        long comments = postCommentRepository.countByPostId(post.getId());
        long shares = postRepository.countByParentPostId(post.getId());
        double engagement = likes + (comments * 5.0) + (shares * 10.0);

        long hours = ChronoUnit.HOURS.between(post.getCreatedAt(), LocalDateTime.now());

        return (weight + Math.log1p(engagement)) / Math.pow(hours + 2.0, 1.5);
    }
}