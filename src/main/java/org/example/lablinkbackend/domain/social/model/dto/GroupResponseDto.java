package org.example.lablinkbackend.domain.social.model.dto;

import lombok.Data;

@Data
public class GroupResponseDto {
    private Long id;
    private String name;
    private String description;
    private String avatarUrl;
    private Long ownerId;
    private String ownerName;
}
