package org.example.lablinkbackend.domain.social.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.lablinkbackend.common.exception.LabLinkException;
import org.example.lablinkbackend.common.exception.ResourceNotFoundException;
import org.example.lablinkbackend.domain.social.model.dto.GroupRequestDto;
import org.example.lablinkbackend.domain.social.model.dto.GroupResponseDto;
import org.example.lablinkbackend.domain.social.model.entity.Group;
import org.example.lablinkbackend.domain.social.model.entity.GroupMember;
import org.example.lablinkbackend.domain.social.model.entity.GroupMemberId;
import org.example.lablinkbackend.domain.social.repository.GroupMemberRepository;
import org.example.lablinkbackend.domain.social.repository.GroupRepository;
import org.example.lablinkbackend.domain.user.model.entity.User;
import org.example.lablinkbackend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public GroupResponseDto createGroup(GroupRequestDto dto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Group group = new Group();
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setAvatarUrl(dto.getAvatarUrl());
        group.setOwner(owner);
        Group savedGroup = groupRepository.save(group);

        GroupMember membership = new GroupMember();
        membership.setId(new GroupMemberId(savedGroup.getId(), owner.getId()));
        membership.setGroup(savedGroup);
        membership.setUser(owner);
        membership.setRole("OWNER");
        groupMemberRepository.save(membership);

        return mapToDto(savedGroup);
    }

    public void joinGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        GroupMemberId id = new GroupMemberId(groupId, userId);
        if (groupMemberRepository.existsById(id)) {
            throw new LabLinkException("Already a member");
        }

        GroupMember member = new GroupMember();
        member.setId(id);
        member.setGroup(group);
        member.setUser(user);
        member.setRole("MEMBER");
        groupMemberRepository.save(member);
    }

    private GroupResponseDto mapToDto(Group group) {
        GroupResponseDto dto = new GroupResponseDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setAvatarUrl(group.getAvatarUrl());

        if (group.getOwner() != null) {
            dto.setOwnerId(group.getOwner().getId());
            dto.setOwnerName(group.getOwner().getFirstName() + " " + group.getOwner().getLastName());
        }

        return dto;
    }

    public GroupResponseDto getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        return mapToDto(group);
    }

    public List<GroupResponseDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void deleteGroup(Long id, Long currentUserId) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        if (!group.getOwner().getId().equals(currentUserId)) {
            throw new LabLinkException("You are not the owner of this group");
        }

        groupRepository.delete(group);
    }

    @Transactional
    public void subscribeToGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        GroupMemberId memberId = new GroupMemberId(groupId, userId);

        if (groupMemberRepository.existsById(memberId)) {
            throw new RuntimeException("You are already subscribed to this group");
        }

        GroupMember membership = new GroupMember();
        membership.setId(memberId);
        membership.setGroup(group);
        membership.setUser(user);
        membership.setRole("MEMBER");
        membership.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(membership);
    }
}