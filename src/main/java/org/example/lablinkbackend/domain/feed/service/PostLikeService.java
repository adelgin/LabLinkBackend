package org.example.lablinkbackend.domain.feed.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.common.exception.ResourceNotFoundException;
import org.example.lablinkbackend.domain.feed.model.entity.Post;
import org.example.lablinkbackend.domain.feed.model.entity.PostLike;
import org.example.lablinkbackend.domain.feed.repository.PostLikeRepository;
import org.example.lablinkbackend.domain.feed.repository.PostRepository;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public boolean toggleLike(Long postId, Long userId) {
        PostLike.PostLikeId id = new PostLike.PostLikeId(postId, userId);

        if (postLikeRepository.existsById(id)) {
            postLikeRepository.deleteById(id);
            return false;
        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            PostLike like = new PostLike(id, post, user);
            postLikeRepository.save(like);
            return true;
        }
    }
}
