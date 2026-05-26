package org.example.lablinkbackend.domain.feed.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class FeedContextDto {
    private Long userId;
    private List<Long> followingIds;
    private List<Integer> tagIds;
    private List<Long> groupIds;

    public FeedContextDto(Long userId, List<Long> followingIds, List<Integer> tagIds, List<Long> groupIds) {
        this.userId = userId;
        this.followingIds = followingIds != null ? followingIds : new ArrayList<>();
        this.tagIds = tagIds != null ? tagIds : new ArrayList<>();
        this.groupIds = groupIds != null ? groupIds : new ArrayList<>();
    }
}