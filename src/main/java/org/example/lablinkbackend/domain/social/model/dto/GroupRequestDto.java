package org.example.lablinkbackend.domain.social.model.dto;

import lombok.Data;

@Data
public class GroupRequestDto {
    private String name;
    private String description;
    private String avatarUrl;
}
