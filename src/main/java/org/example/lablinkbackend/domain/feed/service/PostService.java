package org.example.lablinkbackend.domain.feed.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.common.exception.ResourceNotFoundException;
import org.example.lablinkbackend.domain.feed.model.dto.PostRequestDto;
import org.example.lablinkbackend.domain.feed.model.dto.PostResponseDto;
import org.example.lablinkbackend.domain.feed.model.entity.Post;
import org.example.lablinkbackend.domain.feed.model.entity.PostAttachment;
import org.example.lablinkbackend.domain.feed.model.entity.PostComment;
import org.example.lablinkbackend.domain.feed.repository.PostAttachmentRepository;
import org.example.lablinkbackend.domain.feed.repository.PostCommentRepository;
import org.example.lablinkbackend.domain.feed.repository.PostLikeRepository;
import org.example.lablinkbackend.domain.feed.repository.PostRepository;
import org.example.lablinkbackend.domain.social.model.entity.Group;
import org.example.lablinkbackend.domain.social.repository.GroupRepository;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PostAttachmentRepository attachmentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;

    public Page<PostResponseDto> getUserPosts(Long userId, Pageable pageable, Long currentUserId) {
        return postRepository.findAllByAuthorId(userId, pageable)
                .map(post -> convertToDto(post, currentUserId));
    }

    public Page<PostResponseDto> getGroupPosts(Long groupId, Pageable pageable, Long currentUserId) {
        return postRepository.findAllByGroupIdOrderByCreatedAtDesc(groupId, pageable)
                .map(post -> convertToDto(post, currentUserId));
    }

    public PostResponseDto createPost(PostRequestDto dto, Long currentUserId) {
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = new Post();
        post.setAuthor(author);
        post.setContent(dto.getContent());
        post.setCreatedAt(LocalDateTime.now());

        // Обработка репоста
        if (dto.getParentPostId() != null) {
            Post parent = postRepository.findById(dto.getParentPostId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent post not found"));
            post.setParentPost(parent);
        }

        // Обработка группы
        if (dto.getGroupId() != null) {
            Group group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            post.setGroup(group);
        }

        Post savedPost = postRepository.save(post);

        // Обработка вложений (файлов)
        if (dto.getFileUrls() != null && !dto.getFileUrls().isEmpty()) {
            List<PostAttachment> attachments = dto.getFileUrls().stream()
                    .map(url -> {
                        PostAttachment attachment = new PostAttachment();
                        attachment.setPost(savedPost);
                        attachment.setFileUrl(url);
                        return attachment;
                    }).collect(Collectors.toList());
            attachmentRepository.saveAll(attachments);
            savedPost.setAttachments(attachments);
        }

        return convertToDto(savedPost, currentUserId);
    }

    public void addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        postCommentRepository.save(comment);
    }

    public PostResponseDto convertToDto(Post post, Long currentUserId) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());

        // Логика отображения автора/группы
        if (post.getGroup() != null) {
            dto.setGroupId(post.getGroup().getId());
            dto.setGroupName(post.getGroup().getName());
            dto.setGroupPost(true);
            dto.setAuthorId(post.getGroup().getId());
            dto.setAuthorName(post.getGroup().getName());
        } else {
            dto.setGroupPost(false);
            dto.setAuthorId(post.getAuthor().getId());
            dto.setAuthorName(post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName());
        }

        // Вложения
        if (post.getAttachments() != null) {
            dto.setFileUrls(post.getAttachments().stream()
                    .map(PostAttachment::getFileUrl)
                    .collect(Collectors.toList()));
        }

        // Статистика (Лайки, Репосты, Комменты)
        dto.setLikesCount(postLikeRepository.countByPostId(post.getId()));
        dto.setCommentsCount(postCommentRepository.countByPostId(post.getId()));
        dto.setSharesCount(postRepository.countByParentPostId(post.getId()));

        if (currentUserId != null) {
            dto.setLikedByMe(postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId));
        }

        // Маппинг комментариев для фронта
        dto.setComments(postCommentRepository.findAllByPostIdOrderByCreatedAtAsc(post.getId()).stream()
                .map(c -> new PostResponseDto.CommentResponseDto(
                        c.getId(),
                        c.getUser().getId(),
                        c.getUser().getFirstName() + " " + c.getUser().getLastName(),
                        c.getContent(),
                        c.getCreatedAt()))
                .collect(Collectors.toList()));

        // Рекурсивный маппинг репоста
        if (post.getParentPost() != null) {
            dto.setParentPost(convertToDto(post.getParentPost(), currentUserId));
        }

        return dto;
    }
}