package org.example.lablinkbackend.domain.feed.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class FeedContextDto {
    private Long userId;
    private List<Long> followingIds;
    private List<Integer> tagIds;
    private List<Long> groupIds;
}
