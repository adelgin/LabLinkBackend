package org.example.lablinkbackend.domain.feed.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.common.exception.ResourceNotFoundException;
import org.example.lablinkbackend.domain.feed.model.entity.Post;
import org.example.lablinkbackend.domain.feed.model.entity.PostComment;
import org.example.lablinkbackend.domain.feed.repository.PostCommentRepository;
import org.example.lablinkbackend.domain.feed.repository.PostRepository;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        postCommentRepository.save(comment);
    }

    public void updateComment(Long commentId, Long userId, String newContent) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not the author of this comment!");
        }

        comment.setContent(newContent);
    }

    public void deleteComment(Long commentId, Long userId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not the author of this comment!");
        }

        postCommentRepository.delete(comment);
    }
}